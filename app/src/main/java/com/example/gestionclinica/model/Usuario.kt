package com.example.gestionclinica.model

enum class RolUsuario {
    FUNCIONARIO,
    ADMINISTRATIVO,
    JEFATURA,
    RECURSOS_HUMANOS,
    ADMINISTRADOR_SISTEMA,
    TI_CIBERSEGURIDAD,
    SUPERVISOR_INSTITUCIONAL
}

data class Usuario(
    val id: Int,
    val nombre: String,
    val rut: String,
    val rol: RolUsuario,
    val activo: Boolean
)