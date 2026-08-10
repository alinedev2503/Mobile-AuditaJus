package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "evidence_photos")
data class EvidencePhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: Long,
    val photoUri: String,
    val label: String, // "Contrato", "Conta de Luz", "Conversa de WhatsApp", "Outro"
    val extractedText: String = "",
    val analyzedAmount: Double = 0.0,
    val timestamp: Long = System.currentTimeMillis()
)
