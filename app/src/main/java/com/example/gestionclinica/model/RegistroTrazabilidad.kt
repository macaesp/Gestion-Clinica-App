package com.example.gestionclinica.model
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

// Sin clave foránea a propósito: el registro de auditoría debe conservarse
// aunque el documento o el usuario se eliminen en el futuro.
@Entity(
    tableName = "registros_trazabilidad",
    indices = [Index("documentoId")]
)
data class RegistroTrazabilidad(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val documentoId: Int,
    val usuarioId: Int,
    val fechaHora: String,
    val accionRealizada: String,
    val detalles: String?
)