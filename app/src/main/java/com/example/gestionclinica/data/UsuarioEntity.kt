package com.example.gestionclinica.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.model.Usuario


@Entity(
    tableName = "usuarios",
    indices = [Index(value = ["rut"], unique = true)]
)
data class UsuarioEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val nombre: String,
    val rut: String,
    val rol: String,
    val activo: Boolean,
    val passwordHash: String,
    val salt: String,
    val centroAsignado: String? = null
)

// El modelo que usa la UI no incluye hash ni sal.
fun UsuarioEntity.toModel(): Usuario = Usuario(
    id = id,
    nombre = nombre,
    rut = rut,
    rol = RolUsuario.valueOf(rol),
    activo = activo,
    centroAsignado = centroAsignado
)
