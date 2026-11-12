package com.example.gestionclinica.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestionclinica.model.RegistroTrazabilidad
import kotlinx.coroutines.flow.Flow

@Dao
interface TrazabilidadDao {
    // Desde mas antiguo hasta el mas nuevo
    @Query("SELECT * FROM registros_trazabilidad ORDER BY id")
    fun observarTodos(): Flow<List<RegistroTrazabilidad>>

    @Insert
    suspend fun insertar(registro: RegistroTrazabilidad): Long
}