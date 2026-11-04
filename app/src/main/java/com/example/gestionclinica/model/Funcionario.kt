package com.example.gestionclinica.model

data class Funcionario(
    val id: Int,
    val rut: String,
    val nombre: String,
    val centroSalud: String,
    val cargo: String,
    val profesion: String,
    val tipoContrato: String,
    val fechaIngreso: String,
    val situacionContractual: String,
    val estado: String
)