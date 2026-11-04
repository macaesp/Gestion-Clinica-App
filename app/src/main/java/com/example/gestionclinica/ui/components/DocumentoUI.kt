package com.example.gestionclinica.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Work
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.gestionclinica.model.EspecificacionDocumento

fun EspecificacionDocumento.nombreCategoria(): String = when (this) {
    is EspecificacionDocumento.Laboral -> "Laboral"
    is EspecificacionDocumento.Permiso -> "Permiso"
    is EspecificacionDocumento.Capacitacion -> "Capacitación"
    is EspecificacionDocumento.General -> "General"
}

fun EspecificacionDocumento.icono(): ImageVector = when (this) {
    is EspecificacionDocumento.Laboral -> Icons.Default.Work
    is EspecificacionDocumento.Permiso -> Icons.Default.EventAvailable
    is EspecificacionDocumento.Capacitacion -> Icons.Default.School
    is EspecificacionDocumento.General -> Icons.Default.Description
}

/** Texto corto que describe el documento en las listas (o null si no hay). */
fun EspecificacionDocumento.resumen(): String? = when (this) {
    is EspecificacionDocumento.Laboral -> subtipo.ifBlank { null }
    is EspecificacionDocumento.Permiso -> tipoSolicitud.ifBlank { null }
    is EspecificacionDocumento.Capacitacion -> nombreCurso.ifBlank { null }
    is EspecificacionDocumento.General -> null
}