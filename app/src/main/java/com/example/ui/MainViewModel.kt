package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.util.CloudSyncManager
import kotlinx.coroutines.launch
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

    fun createNewCase(
        title: String,
        category: String,
        description: String,
        authorName: String = "Dr. Roberto Silva",
        defendantName: String = ""
    ) {
        viewModelScope.launch {
            val newCase = CaseEntity(
                title = title,
                category = category,
                description = description,
                status = "UPLOAD",
                date = "Hoje",
                authorName = authorName,
                defendantName = defendantName
            )
            val newId = repository.insertCase(newCase)
            cloudSyncManager.syncCaseToCloud(newCase)
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

    fun addHearingDeadline(title: String, date: String, time: String, type: String, notes: String, caseId: Long? = null) {
        viewModelScope.launch {
            repository.addHearingDeadline(
                HearingDeadlineEntity(
                    caseId = caseId,
                    title = title,
                    dateString = date,
                    timeString = time,
                    locationOrNotes = notes,
                    type = type
                )
            )
        }
    }

    fun toggleHearingDeadline(item: HearingDeadlineEntity) {
        viewModelScope.launch {
            repository.toggleHearingDeadlineCompleted(item)
        }
    }

    fun updateManualValues(caseId: Long, matDamage: Double, inpc: Double, juros: Double, moral: Double) {
        viewModelScope.launch {
            val caseEntity = repository.allCases.firstOrNull()?.find { it.id == caseId } ?: return@launch
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

    fun exportPetitionPdf(context: Context, caseEntity: CaseEntity): File {
        val file = PdfExportManager.generatePetitionPdf(context, caseEntity)
        return file
    }

    fun sharePetitionPdf(context: Context, caseEntity: CaseEntity) {
        val file = PdfExportManager.generatePetitionPdf(context, caseEntity)
        PdfExportManager.sharePdfFile(context, file)
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
