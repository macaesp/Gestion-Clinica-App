package com.example.gestionclinica.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestionclinica.model.Funcionario
import com.example.gestionclinica.model.RegistroTrazabilidad
import kotlinx.coroutines.flow.Flow

@Dao
interface FuncionarioDao {
    @Query("SELECT * FROM funcionarios ORDER BY nombre COLLATE NOCASE")
    fun observarTodos(): Flow<List<Funcionario>>

    @Insert
    suspend fun insertar(funcionario: Funcionario): Long
}

@Dao
interface DocumentoDao {
    @Query("SELECT * FROM documentos ORDER BY id")
    fun observarTodos(): Flow<List<DocumentoEntity>>

    @Query("SELECT MAX(id) FROM documentos")
    suspend fun maxId(): Int?

    @Insert
    suspend fun insertar(documento: DocumentoEntity): Long
}

@Dao
interface TrazabilidadDao {
    // Orden cronológico (el más antiguo primero); la pantalla lo invierte.
    @Query("SELECT * FROM registros_trazabilidad ORDER BY id")
    fun observarTodos(): Flow<List<RegistroTrazabilidad>>

    @Insert
    suspend fun insertar(registro: RegistroTrazabilidad): Long
}