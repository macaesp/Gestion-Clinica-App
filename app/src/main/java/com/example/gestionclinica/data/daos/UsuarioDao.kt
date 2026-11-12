package com.example.gestionclinica.data.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.gestionclinica.data.UsuarioEntity

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE rut = :rut LIMIT 1")
    suspend fun buscarPorRut(rut: String): UsuarioEntity?

    @Query("SELECT * FROM usuarios ORDER BY nombre COLLATE NOCASE")
    suspend fun listarTodos(): List<UsuarioEntity>

    @Insert
    suspend fun insertar(usuario: UsuarioEntity): Long

    @Query("UPDATE usuarios SET activo = :activo WHERE id = :id")
    suspend fun actualizarEstado(id: Int, activo: Boolean): Int

    @Query("UPDATE usuarios SET centroAsignado = :centro WHERE id = :id")
    suspend fun actualizarCentro(id: Int, centro: String?): Int
}
