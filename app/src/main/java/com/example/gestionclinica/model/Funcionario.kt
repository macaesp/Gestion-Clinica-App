package com.example.gestionclinica.model
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "funcionarios",
    indices = [Index(value = ["rut"], unique = true)]
)
data class Funcionario(
    @PrimaryKey(autoGenerate = true)
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