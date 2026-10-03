package com.example.gestionclinica.model

data class Funcionario (
    val id: Int,
    val nombre: String,
    val rut: String,
    val cargo: String,
    val establecimiento: String,
    val estado: String
)