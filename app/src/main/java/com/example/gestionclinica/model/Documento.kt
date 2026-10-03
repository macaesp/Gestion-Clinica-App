package com.example.gestionclinica.model

data class Documento (
    val id: Int,
    val nombre: String,
    val tipo: String,
    val funcionario: String,
    val estado: String
)