package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "petition_templates")
data class PetitionTemplateEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val category: String, // "Consumidor", "Energia", "Telefonia", "Voos", "FGTS", "Trabalhista"
    val title: String,
    val description: String,
    val defaultFatos: String,
    val defaultFundamentos: String,
    val defaultPedidos: String
)
