package com.example.gestionclinica.ui.components
import com.example.gestionclinica.model.RolUsuario

fun RolUsuario?.puedeRegistrarDocumentos(): Boolean =
    this == RolUsuario.RECURSOS_HUMANOS ||
            this == RolUsuario.ADMINISTRADOR_SISTEMA ||
            this == RolUsuario.JEFATURA

fun RolUsuario?.puedeVerAuditoria(): Boolean =
    this == RolUsuario.ADMINISTRADOR_SISTEMA || this == RolUsuario.TI_CIBERSEGURIDAD

fun RolUsuario.etiqueta(): String = when (this) {
    RolUsuario.FUNCIONARIO -> "Funcionario"
    RolUsuario.ADMINISTRATIVO -> "Administrativo"
    RolUsuario.JEFATURA -> "Jefatura"
    RolUsuario.RECURSOS_HUMANOS -> "Recursos Humanos"
    RolUsuario.ADMINISTRADOR_SISTEMA -> "Administrador del sistema"
    RolUsuario.TI_CIBERSEGURIDAD -> "TI y Ciberseguridad"
    RolUsuario.SUPERVISOR_INSTITUCIONAL -> "Supervisor institucional"
}

fun RolUsuario?.puedeGestionarFuncionarios(): Boolean =
    this == RolUsuario.RECURSOS_HUMANOS || this == RolUsuario.ADMINISTRADOR_SISTEMA