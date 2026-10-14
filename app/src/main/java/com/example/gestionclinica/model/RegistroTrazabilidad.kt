package com.example.gestionclinica.model

data class RegistroTrazabilidad(
    val id: Int,
    val documentoId: Int,
    val usuarioId: Int,
    val fechaHora: String,
    val accionRealizada: String,
    val detalles: String?
)