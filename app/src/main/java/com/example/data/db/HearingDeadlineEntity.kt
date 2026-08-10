package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hearings_deadlines")
data class HearingDeadlineEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val caseId: Long?,
    val title: String,
    val dateString: String, // e.g. "15/11/2026"
    val timeString: String, // e.g. "14:30"
    val locationOrNotes: String, // e.g. "Juizado Especial Cível - Sala 3"
    val type: String, // "HEARING" (Audiência) or "DEADLINE" (Prazo Crítico)
    val isCompleted: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
)
