package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cases")
data class CaseEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val description: String,
    val category: String, // "Energia", "Telefonia", "FGTS", "Trabalhista", "Voos", "Outros"
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
    val createdAt: Long = System.currentTimeMillis()
)
