package com.example.gestionclinica.model

sealed class EspecificacionDocumento {

    data class Laboral(
        val subtipo: String
    ) : EspecificacionDocumento()

    data class Permiso(
        val tipoSolicitud: String,
        val respaldo: String,
        val fechaInicio: String,
        val fechaFin: String,
        val estadoAprobacion: String
    ) : EspecificacionDocumento()

    data class Capacitacion(
        val nombreCurso: String,
        val certificadoRespaldo: String,
        val cantidadHoras: Int,
        val estadoValidacion: String
    ) : EspecificacionDocumento()

    object General : EspecificacionDocumento()
}

data class Documento(
    val id: Int,
    val funcionarioId: Int,
    val usuarioCargaId: Int,
    val fechaCarga: String,
    val estado: String,
    val clasificacion: EspecificacionDocumento
)