package com.example.gestionclinica.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.model.EspecificacionDocumento

/**
 * Tabla plana de documentos. La jerarquía sealed [EspecificacionDocumento]
 * se guarda en una columna [tipo] más las columnas propias de cada tipo
 * (nulas cuando no aplican).
 */
@Entity(
    tableName = "documentos",
    indices = [Index("funcionarioId")]
)
data class DocumentoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val funcionarioId: Int,
    val usuarioCargaId: Int,
    val fechaCarga: String,
    val estado: String,
    val tipo: String,
    // Laboral
    val subtipoLaboral: String? = null,
    // Permiso
    val tipoSolicitud: String? = null,
    val respaldo: String? = null,
    val fechaInicio: String? = null,
    val fechaFin: String? = null,
    val estadoAprobacion: String? = null,
    // Capacitación
    val nombreCurso: String? = null,
    val certificadoRespaldo: String? = null,
    val cantidadHoras: Int? = null,
    val estadoValidacion: String? = null,
    // Escaneo (rutas de archivo; la lista se guarda como JSON, ver Converters)
    val paginasEscaneadas: List<String> = emptyList(),
    val pdfEscaneado: String? = null
)

private const val TIPO_LABORAL = "Laboral"
private const val TIPO_PERMISO = "Permiso"
private const val TIPO_CAPACITACION = "Capacitacion"
private const val TIPO_GENERAL = "General"

fun Documento.toEntity(): DocumentoEntity {
    val base = DocumentoEntity(
        id = id,
        funcionarioId = funcionarioId,
        usuarioCargaId = usuarioCargaId,
        fechaCarga = fechaCarga,
        estado = estado,
        tipo = TIPO_GENERAL,
        paginasEscaneadas = paginasEscaneadas,
        pdfEscaneado = pdfEscaneado
    )
    return when (val c = clasificacion) {
        is EspecificacionDocumento.Laboral -> base.copy(
            tipo = TIPO_LABORAL,
            subtipoLaboral = c.subtipo
        )
        is EspecificacionDocumento.Permiso -> base.copy(
            tipo = TIPO_PERMISO,
            tipoSolicitud = c.tipoSolicitud,
            respaldo = c.respaldo,
            fechaInicio = c.fechaInicio,
            fechaFin = c.fechaFin,
            estadoAprobacion = c.estadoAprobacion
        )
        is EspecificacionDocumento.Capacitacion -> base.copy(
            tipo = TIPO_CAPACITACION,
            nombreCurso = c.nombreCurso,
            certificadoRespaldo = c.certificadoRespaldo,
            cantidadHoras = c.cantidadHoras,
            estadoValidacion = c.estadoValidacion
        )
        EspecificacionDocumento.General -> base
    }
}

fun DocumentoEntity.toModel(): Documento {
    val clasificacion = when (tipo) {
        TIPO_LABORAL -> EspecificacionDocumento.Laboral(subtipo = subtipoLaboral.orEmpty())
        TIPO_PERMISO -> EspecificacionDocumento.Permiso(
            tipoSolicitud = tipoSolicitud.orEmpty(),
            respaldo = respaldo.orEmpty(),
            fechaInicio = fechaInicio.orEmpty(),
            fechaFin = fechaFin.orEmpty(),
            estadoAprobacion = estadoAprobacion.orEmpty()
        )
        TIPO_CAPACITACION -> EspecificacionDocumento.Capacitacion(
            nombreCurso = nombreCurso.orEmpty(),
            certificadoRespaldo = certificadoRespaldo.orEmpty(),
            cantidadHoras = cantidadHoras ?: 0,
            estadoValidacion = estadoValidacion.orEmpty()
        )
        else -> EspecificacionDocumento.General
    }
    return Documento(
        id = id,
        funcionarioId = funcionarioId,
        usuarioCargaId = usuarioCargaId,
        fechaCarga = fechaCarga,
        estado = estado,
        clasificacion = clasificacion,
        paginasEscaneadas = paginasEscaneadas,
        pdfEscaneado = pdfEscaneado
    )
}