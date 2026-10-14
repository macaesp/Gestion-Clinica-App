package com.example.gestionclinica.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.model.EspecificacionDocumento
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DocumentoViewModel : ViewModel() {
    // CAMPOS GENERALES
    var tipo by mutableStateOf("")
        private set
    var funcionarioId by mutableStateOf<Int?>(null)
        private set
    var estado by mutableStateOf("")
        private set

    // CAMPOS ESPECÍFICOS: Laboral
    var subtipoLaboral by mutableStateOf("")
        private set

    // CAMPOS ESPECÍFICOS: Permiso
    var tipoSolicitud by mutableStateOf("")
        private set
    var respaldo by mutableStateOf("")
        private set
    var fechaInicio by mutableStateOf("")
        private set
    var fechaFin by mutableStateOf("")
        private set
    var estadoAprobacion by mutableStateOf("")
        private set

    // CAMPOS ESPECÍFICOS: Capacitacion
    var nombreCurso by mutableStateOf("")
        private set
    var certificadoRespaldo by mutableStateOf("")
        private set
    var cantidadHoras by mutableStateOf("")
        private set
    var estadoValidacion by mutableStateOf("")
        private set

    // ERRORES Y MENSAJES
    var errorTipo by mutableStateOf<String?>(null)
        private set
    var errorFuncionario by mutableStateOf<String?>(null)
        private set
    var errorEstado by mutableStateOf<String?>(null)
        private set
    var mensajeExito by mutableStateOf<String?>(null)
        private set

    val documentos = mutableStateListOf<Documento>()
    var documentoSeleccionado by mutableStateOf<Documento?>(null)
        private set

    // MÉTODOS DE CAMBIO GENERALES
    fun cambiarTipo(valor: String) { tipo = valor; errorTipo = null }
    fun cambiarFuncionario(id: Int?) { funcionarioId = id; errorFuncionario = null }
    fun cambiarEstado(valor: String) { estado = valor; errorEstado = null }

    // MÉTODOS DE CAMBIO ESPECÍFICOS
    fun cambiarSubtipoLaboral(valor: String) { subtipoLaboral = valor }
    fun cambiarTipoSolicitud(valor: String) { tipoSolicitud = valor }
    fun cambiarRespaldo(valor: String) { respaldo = valor }
    fun cambiarFechaInicio(valor: String) { fechaInicio = valor }
    fun cambiarFechaFin(valor: String) { fechaFin = valor }
    fun cambiarEstadoAprobacion(valor: String) { estadoAprobacion = valor }
    fun cambiarNombreCurso(valor: String) { nombreCurso = valor }
    fun cambiarCertificadoRespaldo(valor: String) { certificadoRespaldo = valor }
    fun cambiarCantidadHoras(valor: String) { cantidadHoras = valor }
    fun cambiarEstadoValidacion(valor: String) { estadoValidacion = valor }

    fun seleccionarDocumento(documento: Documento) {
        documentoSeleccionado = documento
    }

    fun obtenerDocumentosPorFuncionario(idFuncionario: Int): List<Documento> {
        return documentos.filter { it.funcionarioId == idFuncionario }
    }

    fun prepararRegistroParaFuncionario(idFuncionario: Int) {
        limpiarFormulario()
        funcionarioId = idFuncionario
    }

    fun limpiarFormulario() {
        tipo = ""; funcionarioId = null; estado = ""
        subtipoLaboral = ""
        tipoSolicitud = ""; respaldo = ""; fechaInicio = ""; fechaFin = ""; estadoAprobacion = ""
        nombreCurso = ""; certificadoRespaldo = ""; cantidadHoras = ""; estadoValidacion = ""
        errorTipo = null; errorFuncionario = null; errorEstado = null; mensajeExito = null
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun guardarDocumento(usuarioLogueadoId: Int = 1): Boolean {
        if (tipo.isBlank()) { errorTipo = "Requerido"; return false }
        if (funcionarioId == null) { errorFuncionario = "Requerido"; return false }
        if (estado.isBlank()) { errorEstado = "Requerido"; return false }

        val especificacion = when (tipo) {
            "Laboral" -> EspecificacionDocumento.Laboral(subtipo = subtipoLaboral)
            "Permiso" -> EspecificacionDocumento.Permiso(
                tipoSolicitud = tipoSolicitud,
                respaldo = respaldo,
                fechaInicio = fechaInicio,
                fechaFin = fechaFin,
                estadoAprobacion = estadoAprobacion
            )
            "Capacitacion" -> EspecificacionDocumento.Capacitacion(
                nombreCurso = nombreCurso,
                certificadoRespaldo = certificadoRespaldo,
                cantidadHoras = cantidadHoras.toIntOrNull() ?: 0,
                estadoValidacion = estadoValidacion
            )
            else -> EspecificacionDocumento.General
        }

        val nuevoDocumento = Documento(
            id = documentos.size + 1,
            funcionarioId = funcionarioId!!,
            usuarioCargaId = usuarioLogueadoId,
            fechaCarga = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
            estado = estado,
            clasificacion = especificacion
        )

        documentos.add(nuevoDocumento)
        documentoSeleccionado = nuevoDocumento
        return true
    }
}