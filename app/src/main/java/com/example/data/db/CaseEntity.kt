package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // "Energia", "Telefonia", "Empréstimos Bancários", "Repetição em Dobro", "FGTS", "Trabalhista", "Voos", "Outros"
    val status: String, // "UPLOAD", "ANALYSING", "PDF_READY", "SENT_TO_COURT"
    val processNumber: String? = null,
    val date: String, // e.g. "Oct 24, 2023"
    val historicalValue: Double = 0.0,
    val inpcCorrection: Double = 0.0,
    val defaultInterest: Double = 0.0,
    val subtotalUpdated: Double = 0.0,
    val suggestedMoralDamages: Double = 0.0,
    val legalBasis: String = "", // e.g. "Art. 42, CDC | Súmula 297, STJ"
    val fatosText: String = "",
    val fundamentosText: String = "",
    val pedidosText: String = "",
    val authorName: String = "Requerente",
    val authorCpf: String = "",
    val defendantName: String = "",
    val calculationType: String = "PADRAO", // "PADRAO", "REPETICAO_DOBRO", "EMPRESTIMO_BANCARIO", "TELECOM_SERVICOS"
    val isRepeticaoEmDobro: Boolean = false,
    val bankContractRate: Double = 0.0, // Taxa contratada a.m. (Ex: 8.5%)
    val bacenAverageRate: Double = 0.0,  // Taxa média BACEN a.m. (Ex: 2.1%)
    val monthsCalculated: Int = 12,
    val rawGeminiJson: String = "", // Armazena localmente em Room o JSON retornado pelo Gemini AI
    val identifiedAbuseSummary: String = "",
    val confidenceScore: Double = 1.0,
    val createdAt: Long = System.currentTimeMillis()
)
