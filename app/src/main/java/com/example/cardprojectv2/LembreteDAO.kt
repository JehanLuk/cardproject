package com.example.cardprojectv2

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LembreteDAO {
    @Insert
    suspend fun inserirLembrete(lembrete: Lembrete)

    @Query("SELECT * FROM lembrete")
    suspend fun obterTodosLembretes(): List<Lembrete>

    @Delete
    suspend fun deletarLembrete(lembrete: Lembrete)
}
