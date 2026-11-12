package com.example.gestionclinica.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.gestionclinica.data.DocumentoEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentoDao {
    @Query("SELECT * FROM documentos ORDER BY id")
    fun observarTodos(): Flow<List<DocumentoEntity>>

    @Query("SELECT MAX(id) FROM documentos")
    suspend fun maxId(): Int?

    @Insert
    suspend fun insertar(documento: DocumentoEntity): Long

    @Update
    suspend fun actualizar(documento: DocumentoEntity)
}
