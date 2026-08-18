package com.example.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.example.BuildConfig
import com.example.data.db.AppDatabase
import com.example.data.db.CaseEntity
import com.example.data.db.EvidencePhotoEntity
import com.example.data.db.HearingDeadlineEntity
import com.example.data.db.PetitionTemplateEntity
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class LegalAuditRepository(private val db: AppDatabase) {

    val allCases = db.caseDao().getAllCases()
    val allHearingsDeadlines = db.hearingDeadlineDao().getAllHearingsDeadlines()
    val allTemplates = db.petitionTemplateDao().getAllTemplates()

    fun getCaseById(id: Long) = db.caseDao().getCaseById(id)

    suspend fun insertCase(case: CaseEntity) = withContext(Dispatchers.IO) {
        db.caseDao().insertCase(case)
    }

    suspend fun updateCase(case: CaseEntity) = withContext(Dispatchers.IO) {
        db.caseDao().updateCase(case)
    }
    
    suspend fun deleteCase(case: CaseEntity) = withContext(Dispatchers.IO) {
        db.caseDao().deleteCase(case)
    }

    fun getPhotosForCase(caseId: Long) = db.evidencePhotoDao().getPhotosForCase(caseId)

    suspend fun insertPhoto(photo: EvidencePhotoEntity) = withContext(Dispatchers.IO) {
        db.evidencePhotoDao().insertPhoto(photo)
    }

    suspend fun addEvidencePhoto(photo: EvidencePhotoEntity) = withContext(Dispatchers.IO) {
        db.evidencePhotoDao().insertPhoto(photo)
    }

    suspend fun deletePhoto(photo: EvidencePhotoEntity) = withContext(Dispatchers.IO) {
        db.evidencePhotoDao().deletePhoto(photo)
    }

    suspend fun addHearingDeadline(item: HearingDeadlineEntity) = withContext(Dispatchers.IO) {
        db.hearingDeadlineDao().insert(item)
    }

    suspend fun toggleHearingDeadlineCompleted(item: HearingDeadlineEntity) = withContext(Dispatchers.IO) {
        db.hearingDeadlineDao().update(item.copy(isCompleted = !item.isCompleted))
    }

    suspend fun analyzeCaseWithGemini(context: Context, caseId: Long, userInstructions: String = ""): Result<CaseEntity> = withContext(Dispatchers.IO) {
        try {
            val caseEntity = db.caseDao().getCaseByIdDirect(caseId) ?: return@withContext Result.failure(Exception("Caso não encontrado"))
            val photos = db.evidencePhotoDao().getPhotosForCaseDirect(caseId)

            val prompt = """
                System Instruction:
                Role: You are a World-Class Senior Legal Auditor and Data Scientist specialized in Brazilian Consumer Law (JEC).

                Task: Analyze images of utility bills (electricity, water, phone) or consumer contracts to identify potential overcharges, hidden taxes, or billing errors.

                Context: 
                - Focus on common Brazilian abuses: TUST/TUSD on energy bills, unauthorized "serviços de terceiros" in telecom, or illegal interest rates.
                - Ground your analysis purely on the provided images.
                - Caso Atual: ${caseEntity.title}
                - Instruções do Usuário: $userInstructions

                Constraints:
                - DO NOT provide legal advice. Use a disclaimer: "Auditoria contábil baseada nos dados fornecidos. Consulte um advogado para protocolar a ação."
                - If the image is blurred or data is missing, report "confidence_score": < 0.7.
                - Strictly output valid JSON.

                Output Format:
                {
                  "audit_summary": {
                    "provider_name": "string",
                    "consumer_name": "string",
                    "reference_month": "string",
                    "total_value": 0.0,
                    "identified_abuse": "string",
                    "overcharged_amount": 0.0
                  },
                  "calculation_logic": {
                    "base_value": 0.0,
                    "applied_interest_rate": "SELIC",
                    "total_recoverable": 0.0
                  },
                  "confidence_score": 1.0
                }
            """.trimIndent()

            val generativeModel = com.google.firebase.Firebase.ai.generativeModel(
                modelName = "gemini-3.5-flash",
                generationConfig = com.google.firebase.ai.type.generationConfig {
                    responseMimeType = "application/json"
                    temperature = 0.2f
                }
            )

            val promptContent = com.google.firebase.ai.type.content {
                text(prompt)
                for (photo in photos) {
                    val bitmap = getBitmap(context, photo.photoUri)
                    if (bitmap != null) {
                        image(bitmap)
                    }
                }
            }

            val responseText = try {
                val response = generativeModel.generateContent(promptContent)
                response.text ?: ""
            } catch (e: Exception) {
                e.printStackTrace()
                ""
            }

            if (responseText.isNotBlank()) {
                // Parse JSON
                val cleanJson = responseText.replace(Regex("```json|```"), "").trim()
                try {
                    val jsonObject = org.json.JSONObject(cleanJson)
                    val auditSummaryObj = jsonObject.optJSONObject("audit_summary") ?: org.json.JSONObject()
                    val calcLogicObj = jsonObject.optJSONObject("calculation_logic") ?: org.json.JSONObject()
                    val confidenceScore = jsonObject.optDouble("confidence_score", 1.0)
                    
                    val providerName = auditSummaryObj.optString("provider_name", "Fornecedor Desconhecido")
                    val consumerName = auditSummaryObj.optString("consumer_name", "Consumidor")
                    val refMonth = auditSummaryObj.optString("reference_month", "")
                    val totalValue = auditSummaryObj.optDouble("total_value", 0.0)
                    val identifiedAbuse = auditSummaryObj.optString("identified_abuse", "Nenhum abuso identificado de forma conclusiva.")
                    val overchargedAmount = auditSummaryObj.optDouble("overcharged_amount", 0.0)
                    
                    val baseValue = calcLogicObj.optDouble("base_value", overchargedAmount)
                    val appliedInterestRate = calcLogicObj.optString("applied_interest_rate", "SELIC")
                    val totalRecoverable = calcLogicObj.optDouble("total_recoverable", baseValue)

                    val statusMsg = if (confidenceScore < 0.7) {
                        "Atenção: A imagem estava borrada ou faltam dados (Confiança: ${confidenceScore}). Verifique os anexos."
                    } else {
                        "Auditoria contábil baseada nos dados fornecidos. Consulte um advogado para protocolar a ação."
                    }

                    val fatosText = "O consumidor $consumerName foi cobrado indevidamente pela empresa $providerName na fatura de $refMonth no valor total de R$ $totalValue.\n\nA auditoria identificou o seguinte abuso: $identifiedAbuse"
                    val fundamentosText = statusMsg + "\n\nTaxa Aplicada na lógica de cálculo: $appliedInterestRate"
                    val pedidosText = "1. Reconhecimento da abusividade: $identifiedAbuse\n2. Restituição do valor histórico de R$ $baseValue atualizado para R$ $totalRecoverable."

                    val updatedCase = caseEntity.copy(
                        historicalValue = if (baseValue.isNaN()) 0.0 else baseValue,
                        inpcCorrection = 0.0,
                        defaultInterest = 0.0,
                        suggestedMoralDamages = 0.0,
                        subtotalUpdated = if (totalRecoverable.isNaN()) 0.0 else totalRecoverable,
                        fatosText = fatosText,
                        fundamentosText = fundamentosText,
                        pedidosText = pedidosText,
                        legalBasis = "Auditoria (Confiança: $confidenceScore)",
                        status = "PDF_READY"
                    )

                    db.caseDao().updateCase(updatedCase)
                    return@withContext Result.success(updatedCase)

                } catch (e: Exception) {
                    e.printStackTrace()
                    return@withContext Result.failure(Exception("Erro ao processar JSON da IA Gemini: ${e.message}"))
                }
            } else {
                return@withContext Result.failure(Exception("Resposta vazia da IA Gemini."))
            }

        } catch (e: Exception) {
            e.printStackTrace()
            return@withContext Result.failure(Exception("Erro na análise do caso: ${e.message}"))
        }
    }

    private fun getBitmap(context: Context, uriString: String): Bitmap? {
        if (uriString.isBlank()) return null
        return try {
            val uri = Uri.parse(uriString)
            val inputStream = context.contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun seedInitialDataIfEmpty() = withContext(Dispatchers.IO) {
        val existingCases = db.caseDao().getCaseByIdDirect(1)
        if (existingCases == null) {
            // Seed sample cases from mockup
            val case1 = CaseEntity(
                id = 1,
                title = "Ação Trabalhista Silva / Conta de Luz Abusiva",
                description = "Auditoria de faturas de energia para identificação de classificações tarifárias impróprias e taxas duplicadas.",
                category = "Energia",
                status = "ANALYSING",
                processNumber = null,
                date = "24 de Out, 2023",
                historicalValue = 450.00,
                inpcCorrection = 32.45,
                defaultInterest = 27.00,
                subtotalUpdated = 509.45,
                suggestedMoralDamages = 3000.00,
                legalBasis = "Art. 42, CDC | Súmula 297, STJ",
                fatosText = "O Requerente constatou a cobrança reiterada de taxa de iluminação duplicada e multa indevida por suposto atraso no pagamento de sua conta de energia elétrica residencial.",
                fundamentosText = "Conforme o art. 42 do Código de Defesa do Consumidor, o consumidor cobrado em quantia indevida tem direito à repetição do indébito por valor igual ao dobro do que pagou em excesso. Além disso, a falha contínua da concessionária gera reparação moral pelo desvio produtivo e desgaste sofrido.",
                pedidosText = "1. Citação do fornecedor para responder aos termos da ação;\n2. Restituição em dobro do dano material de R$ 450,00 atualizado para R$ 509,45;\n3. Condenação em R$ 3.000,00 por danos morais punitivo-pedagógicos."
            )
            val case2 = CaseEntity(
                id = 2,
                title = "Revisão FGTS",
                description = "Recálculo do saldo do FGTS com aplicação de índice inflacionário substitutivo da TR.",
                category = "FGTS",
                status = "PDF_READY",
                processNumber = "00123.2023",
                date = "18 de Out, 2023",
                historicalValue = 1250.00,
                inpcCorrection = 180.30,
                defaultInterest = 120.00,
                subtotalUpdated = 1550.30,
                suggestedMoralDamages = 0.00,
                legalBasis = "Lei 8.036/90 | ADI 5090 STF",
                fatosText = "Depósitos do FGTS corrigidos apenas pela TR, gerando perda real do poder de compra no fundo de garantia por tempo de serviço.",
                fundamentosText = "Inconstitucionalidade do uso da TR como índice de atualização de depósitos trabalhistas, conforme tese fixada no STF.",
                pedidosText = "1. Atualização do saldo do FGTS pelo INPC;\n2. Depósito da diferença apurada na conta vinculada."
            )
            val case3 = CaseEntity(
                id = 3,
                title = "Cobrança Indevida Telefonia",
                description = "Ação contra operadora por serviços não autorizados cobrados de forma contínua.",
                category = "Telefonia",
                status = "SENT_TO_COURT",
                processNumber = "0012345-67.2023",
                date = "05 de Set, 2023",
                historicalValue = 350.00,
                inpcCorrection = 25.00,
                defaultInterest = 18.00,
                subtotalUpdated = 393.00,
                suggestedMoralDamages = 2500.00,
                legalBasis = "Art. 14, CDC",
                fatosText = "Lançamento mensal de 'Serviços de Terceiros' e pacote de dados extra jamais contratados pelo consumidor.",
                fundamentosText = "Prática abusiva vedada pelo artigo 39 do CDC e vício na prestação dos serviços.",
                pedidosText = "1. Cancelamento do pacote ilegítimo;\n2. Devolução em dobro;\n3. Danos morais."
            )
            db.caseDao().insertCase(case1)
            db.caseDao().insertCase(case2)
            db.caseDao().insertCase(case3)

            // Seed sample evidence photos
            db.evidencePhotoDao().insertPhoto(
                EvidencePhotoEntity(
                    caseId = 1,
                    photoUri = "",
                    label = "Conta de Luz - Taxa Duplicada",
                    extractedText = "Fatura N° 889231 - Taxa Iluminação R$ 150,00",
                    analyzedAmount = 150.00
                )
            )
            db.evidencePhotoDao().insertPhoto(
                EvidencePhotoEntity(
                    caseId = 1,
                    photoUri = "",
                    label = "Comprovante de Multa Indevida",
                    extractedText = "Atraso Fictício R$ 300,00",
                    analyzedAmount = 300.00
                )
            )

            // Seed sample hearings and deadlines
            db.hearingDeadlineDao().insert(
                HearingDeadlineEntity(
                    caseId = 3,
                    title = "Audiência de Conciliação - Telefonia",
                    dateString = "25/11/2026",
                    timeString = "14:00",
                    locationOrNotes = "Juizado Especial Cível - Sala 02",
                    type = "HEARING",
                    isCompleted = false
                )
            )
            db.hearingDeadlineDao().insert(
                HearingDeadlineEntity(
                    caseId = 1,
                    title = "Prazo para Juntar Comprovante de Residência",
                    dateString = "18/11/2026",
                    timeString = "23:59",
                    locationOrNotes = "Secretaria do JEC Central",
                    type = "DEADLINE",
                    isCompleted = false
                )
            )

            // Seed petition templates
            db.petitionTemplateDao().insertAll(
                listOf(
                    PetitionTemplateEntity(
                        category = "Energia",
                        title = "Cobrança Abusiva de Tarifa de Energia",
                        description = "Modelo para corte indevido de luz, bandeira tarifária incorreta ou queima de aparelhos.",
                        defaultFatos = "O Requerente é usuário do serviço de energia elétrica sob o código de instalação N° X. Ocorre que foi surpreendido com cobrança indevida...",
                        defaultFundamentos = "Aplica-se o Código de Defesa do Consumidor aos serviços públicos prestados por concessionárias (Art. 22 do CDC).",
                        defaultPedidos = "a) Restituição dos valores pagos a maior;\nb) Indenização por danos morais."
                    ),
                    PetitionTemplateEntity(
                        category = "Telefonia",
                        title = "Serviço Não Solicitado em Linha Telefônica",
                        description = "Modelo para pacotes VAS, roaming fictício ou cobrança após cancelamento.",
                        defaultFatos = "O Requerente é titular da linha telefônica N° X e vem sofrendo descontos não autorizados sob o título de serviços de terceiros...",
                        defaultFundamentos = "Art. 39, III do CDC veda o fornecimento de produto ou serviço sem solicitação prévia.",
                        defaultPedidos = "a) Cancelamento definitivo da cobrança;\nb) Devolução do indébito em dobro."
                    ),
                    PetitionTemplateEntity(
                        category = "Voos",
                        title = "Atraso, Cancelamento de Voo ou Extravio de Bagagem",
                        description = "Modelo para problemas com companhias aéreas e falhas de assistência em aeroportos.",
                        defaultFatos = "O Requerente adquiriu passagem aérea para o trecho X-Y, sofrendo atraso superior a 4 horas e ausência de assistência material.",
                        defaultFundamentos = "Responsabilidade objetiva do transportador aéreo (Art. 14 do CDC e Resolução 400 ANAC).",
                        defaultPedidos = "a) Indenização por danos materiais referentes a alimentação/transporte;\nb) Danos morais."
                    )
                )
            )
        }
    }
}
