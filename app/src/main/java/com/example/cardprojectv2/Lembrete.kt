package com.example.cardprojectv2

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.threeten.bp.LocalDateTime

@Entity(tableName = "lembrete")
data class Lembrete(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mensagem: String,
    val dataCriacao: LocalDateTime? = null
)
