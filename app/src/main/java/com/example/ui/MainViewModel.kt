package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.util.CloudSyncManager
import com.example.data.db.CaseEntity
import com.example.data.db.EvidencePhotoEntity
import com.example.data.db.HearingDeadlineEntity
import com.example.data.preferences.UserPreferencesRepository
import com.example.data.preferences.UserSettings
import com.example.data.repository.LegalAuditRepository
import com.example.util.PdfExportManager
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.io.File

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val db = AppDatabase.getInstance(application)
    private val repository = LegalAuditRepository(db)
    private val userPreferences = UserPreferencesRepository(application)
    private val cloudSyncManager = CloudSyncManager(application, db)

    val userSettings: StateFlow<UserSettings> = userPreferences.userSettingsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSettings())

    val cases: StateFlow<List<CaseEntity>> = repository.allCases
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val hearingsDeadlines: StateFlow<List<HearingDeadlineEntity>> = repository.allHearingsDeadlines
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val petitionTemplates = repository.allTemplates
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _selectedCaseId = MutableStateFlow<Long?>(1L)
    val selectedCaseId: StateFlow<Long?> = _selectedCaseId.asStateFlow()

    val currentCase: StateFlow<CaseEntity?> = _selectedCaseId.flatMapLatest { id ->
        if (id == null) flowOf(null) else repository.getCaseById(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentCasePhotos: StateFlow<List<EvidencePhotoEntity>> = _selectedCaseId.flatMapLatest { id ->
        if (id == null) flowOf(emptyList()) else repository.getPhotosForCase(id)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _isAnalyzing = MutableStateFlow(false)
    val isAnalyzing: StateFlow<Boolean> = _isAnalyzing.asStateFlow()

    private val _analysisMessage = MutableStateFlow<String?>(null)
    val analysisMessage: StateFlow<String?> = _analysisMessage.asStateFlow()

    private val _selectedCaseFilter = MutableStateFlow("All")
    val selectedCaseFilter: StateFlow<String> = _selectedCaseFilter.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _guideFilter = MutableStateFlow("Todos")
    val guideFilter: StateFlow<String> = _guideFilter.asStateFlow()

    val favoriteGuides: StateFlow<List<com.example.data.db.FavoriteGuideEntity>> = repository.allFavoriteGuides
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun isGuideFavorite(guideId: String): Flow<Boolean> = repository.isGuideFavorite(guideId)

    fun toggleFavoriteGuide(guideId: String, title: String, category: String, snippet: String, readTimeMinutes: Int = 5, isCurrentlyFavorite: Boolean) {
        viewModelScope.launch {
            val entity = com.example.data.db.FavoriteGuideEntity(
                guideId = guideId,
                title = title,
                category = category,
                snippet = snippet,
                readTimeMinutes = readTimeMinutes
            )
            repository.toggleFavoriteGuide(entity, isCurrentlyFavorite)
        }
    }

    fun removeFavoriteGuide(guideId: String) {
        viewModelScope.launch {
            repository.removeFavoriteGuide(guideId)
        }
    }

    init {
        viewModelScope.launch {
            repository.seedInitialDataIfEmpty()
        }
    }

    fun updateCase(caseEntity: CaseEntity) {
        viewModelScope.launch {
            repository.updateCase(caseEntity)
            cloudSyncManager.syncCaseToCloud(caseEntity)
        }
    }

    fun syncDataWithCloud() {
        viewModelScope.launch {
            if (cloudSyncManager.isUserLoggedIn()) {
                cloudSyncManager.restoreCasesFromCloud()
                cases.value.forEach { 
                    cloudSyncManager.restoreEvidenceFromCloud(it.id) 
                }
            }
        }
    }

    fun selectCase(caseId: Long) {
        _selectedCaseId.value = caseId
    }

    fun setCaseFilter(filter: String) {
        _selectedCaseFilter.value = filter
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setGuideFilter(filter: String) {
        _guideFilter.value = filter
    }

    fun createNewCase(title: String, category: String, date: String = "") {
        viewModelScope.launch {
            val newId = repository.insertCase(CaseEntity(title = title, category = category, date = date, description = "", status = "PENDING"))
            _selectedCaseId.value = newId
        }
    }

    fun addEvidencePhoto(caseId: Long, label: String, photoUri: String = "") {
        viewModelScope.launch {
            val ev = EvidencePhotoEntity(
                caseId = caseId,
                photoUri = photoUri,
                label = label,
                extractedText = "Documento anexado para auditoria"
            )
            val newId = repository.addEvidencePhoto(ev)
            cloudSyncManager.syncEvidenceToCloud(ev.copy(id = newId))
        }
    }

    fun triggerGeminiAnalysis(caseId: Long, userNotes: String = "") {
        viewModelScope.launch {
            _isAnalyzing.value = true
            _analysisMessage.value = "Analisando provas com IA Gemini..."
            
            // Set status to ANALYSING
            val existing = repository.allCases.firstOrNull()?.find { it.id == caseId }
            if (existing != null) {
                repository.updateCase(existing.copy(status = "ANALYSING"))
            }
            val result = repository.analyzeCaseWithGemini(getApplication(), caseId, userNotes)
            _isAnalyzing.value = false
            if (result.isSuccess) {
                _analysisMessage.value = "Auditoria concluída com sucesso! Petição pronta."
            } else {
                _analysisMessage.value = "Erro na análise: ${result.exceptionOrNull()?.message}"
            }
        }
    }

    fun clearAnalysisMessage() {
        _analysisMessage.value = null
    }

    fun addHearingDeadline(item: HearingDeadlineEntity) {
        viewModelScope.launch {
            repository.addHearingDeadline(item)
        }
    }

    fun toggleHearingDeadline(item: HearingDeadlineEntity) {
        viewModelScope.launch {
            repository.toggleHearingDeadlineCompleted(item)
        }
    }

    fun updateManualValues(caseEntity: CaseEntity, matDamage: Double, inpc: Double, juros: Double, moral: Double) {
        viewModelScope.launch {
            val updated = caseEntity.copy(
                historicalValue = matDamage,
                inpcCorrection = inpc,
                defaultInterest = juros,
                subtotalUpdated = matDamage + inpc + juros,
                suggestedMoralDamages = moral
            )
            repository.updateCase(updated)
        }
    }

    fun applyCalculationType(
        caseEntity: CaseEntity,
        calculationType: String,
        months: Int = 12,
        bankContractRate: Double = 8.5,
        bacenAverageRate: Double = 2.1
    ) {
        viewModelScope.launch {
            val mode = when (calculationType) {
                "REPETICAO_DOBRO" -> com.example.util.LegalCalculationEngine.CalculationMode.REPETICAO_DOBRO
                "EMPRESTIMO_BANCARIO" -> com.example.util.LegalCalculationEngine.CalculationMode.EMPRESTIMO_BANCARIO
                "TELECOM_SERVICOS" -> com.example.util.LegalCalculationEngine.CalculationMode.TELECOM_SERVICOS
                else -> com.example.util.LegalCalculationEngine.CalculationMode.PADRAO
            }

            val calcResult = com.example.util.LegalCalculationEngine.calculate(
                historicalValue = caseEntity.historicalValue,
                mode = mode,
                months = months,
                bankContractRate = bankContractRate,
                bacenAverageRate = bacenAverageRate
            )

            val updated = caseEntity.copy(
                calculationType = calculationType,
                isRepeticaoEmDobro = (mode == com.example.util.LegalCalculationEngine.CalculationMode.REPETICAO_DOBRO || mode == com.example.util.LegalCalculationEngine.CalculationMode.TELECOM_SERVICOS),
                bankContractRate = bankContractRate,
                bacenAverageRate = bacenAverageRate,
                monthsCalculated = months,
                inpcCorrection = calcResult.inpcCorrection,
                defaultInterest = calcResult.defaultInterest,
                subtotalUpdated = calcResult.totalRecoverable,
                suggestedMoralDamages = calcResult.suggestedMoralDamages,
                legalBasis = mode.legalArticle
            )
            repository.updateCase(updated)
        }
    }

    fun exportPetitionPdf(context: Context, caseEntity: CaseEntity, signatureBitmap: android.graphics.Bitmap? = null): File {
        val file = PdfExportManager.generatePetitionPdf(context, caseEntity, userSettings.value, signatureBitmap)
        return file
    }

    fun exportDocument(
        context: Context,
        caseEntity: CaseEntity,
        signatureBitmap: android.graphics.Bitmap? = null,
        clientSignatureBitmap: android.graphics.Bitmap? = null,
        exportType: com.example.util.PdfExportManager.ExportDocumentType = com.example.util.PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO,
        watermarkText: String = "LAUDO PERICIAL JURÍDICO",
        showWatermark: Boolean = true
    ): File {
        return PdfExportManager.generateLegalDocument(
            context = context,
            caseEntity = caseEntity,
            userSettings = userSettings.value,
            signatureBitmap = signatureBitmap,
            clientSignatureBitmap = clientSignatureBitmap,
            exportType = exportType,
            watermarkText = watermarkText,
            showWatermark = showWatermark
        )
    }

    fun sharePetitionPdf(
        context: Context,
        caseEntity: CaseEntity,
        signatureBitmap: android.graphics.Bitmap? = null,
        clientSignatureBitmap: android.graphics.Bitmap? = null,
        exportType: com.example.util.PdfExportManager.ExportDocumentType = com.example.util.PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO,
        watermarkText: String = "LAUDO PERICIAL JURÍDICO",
        showWatermark: Boolean = true
    ) {
        val file = PdfExportManager.generateLegalDocument(
            context = context,
            caseEntity = caseEntity,
            userSettings = userSettings.value,
            signatureBitmap = signatureBitmap,
            clientSignatureBitmap = clientSignatureBitmap,
            exportType = exportType,
            watermarkText = watermarkText,
            showWatermark = showWatermark
        )
        val title = when (exportType) {
            com.example.util.PdfExportManager.ExportDocumentType.COMBO_PETICAO_E_PROCURACAO -> "Compartilhar Petição e Procuração PDF"
            com.example.util.PdfExportManager.ExportDocumentType.PROCURACAO_ONLY -> "Compartilhar Procuração Ad Judicia PDF"
            com.example.util.PdfExportManager.ExportDocumentType.PETICAO_ONLY -> "Compartilhar Petição Inicial PDF"
            com.example.util.PdfExportManager.ExportDocumentType.LAUDO_AUDITORIA_E_CALCULO -> "Compartilhar Laudo de Auditoria e Juros PDF"
        }
        PdfExportManager.sharePdfFile(context, file, title)
    }

    fun updateLawyerProfile(
        oabNumber: String,
        oabUf: String,
        lawFirmName: String,
        officeAddress: String,
        officePhone: String,
        logoUri: String,
        useCustomLetterhead: Boolean
    ) {
        viewModelScope.launch {
            userPreferences.updateLawyerProfile(
                oabNumber = oabNumber,
                oabUf = oabUf,
                lawFirmName = lawFirmName,
                officeAddress = officeAddress,
                officePhone = officePhone,
                logoUri = logoUri,
                useCustomLetterhead = useCustomLetterhead
            )
        }
    }

    fun setLoggedIn(isLoggedIn: Boolean, name: String? = null, email: String? = null) {
        viewModelScope.launch {
            userPreferences.setLoggedIn(isLoggedIn, name, email)
            if (isLoggedIn) {
                cloudSyncManager.restoreCasesFromCloud()
            }
        }
    }

    fun setBiometricEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setBiometricEnabled(enabled)
        }
    }

    fun setPushEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setPushEnabled(enabled)
        }
    }

    fun setEmailEnabled(enabled: Boolean) {
        viewModelScope.launch {
            userPreferences.setEmailEnabled(enabled)
        }
    }

    fun setAcceptedTerms(accepted: Boolean) {
        viewModelScope.launch {
            userPreferences.setAcceptedTerms(accepted)
        }
    }

    fun setDarkMode(isDark: Boolean?) {
        viewModelScope.launch {
            userPreferences.setDarkMode(isDark)
        }
    }

    fun setCompletedOnboarding(completed: Boolean) {
        viewModelScope.launch {
            userPreferences.setCompletedOnboarding(completed)
        }
    }
}
