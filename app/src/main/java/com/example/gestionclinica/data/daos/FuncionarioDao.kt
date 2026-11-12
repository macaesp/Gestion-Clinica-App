package com.example.gestionclinica.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestionclinica.model.Funcionario
import kotlinx.coroutines.flow.Flow

@Dao
interface FuncionarioDao {
    @Query("SELECT * FROM funcionarios ORDER BY nombre COLLATE NOCASE")
    fun observarTodos(): Flow<List<Funcionario>>

    @Query("SELECT * FROM funcionarios WHERE UPPER(REPLACE(rut, '.', '')) = UPPER(REPLACE(:rut, '.', '')) LIMIT 1")
    suspend fun buscarPorRutNormalizado(rut: String): Funcionario?

    @Insert
    suspend fun insertar(funcionario: Funcionario): Long
}
