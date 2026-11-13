package com.example.gestionclinica.viewmodel

import android.app.Application
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionclinica.data.AppDatabase
import com.example.gestionclinica.data.toEntity
import com.example.gestionclinica.data.toModel
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.model.EspecificacionDocumento
import com.example.gestionclinica.model.Funcionario
import com.example.gestionclinica.model.Usuario
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.repository.DocumentoRepository
import com.example.gestionclinica.repository.SolicitudCambioRepository
import com.example.gestionclinica.data.VersionDocumentoEntity
import com.example.gestionclinica.security.AccesoporRol
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class DocumentoViewModel(application: Application) : AndroidViewModel(application) {

    private val versionDao = AppDatabase.getInstance(application).versionDocumentoDao()

    private val repository = DocumentoRepository(
        AppDatabase.getInstance(application).documentoDao()
    )
    private val solicitudCambioRepository =
        SolicitudCambioRepository(AppDatabase.getInstance(application))

    // Último id usado; evita repetir ids si se guardan dos documentos seguidos
    // antes de que la BD notifique el cambio.
    private var ultimoId = 0

    // CAMPOS GENERALES
    var tipo by mutableStateOf("")
        private set
    var funcionarioId by mutableStateOf<Int?>(null)
        private set
    var estado by mutableStateOf("")
        private set
    var motivoCambio by mutableStateOf("")
        private set
    var errorMotivoCambio by mutableStateOf<String?>(null)
        private set
    var solicitudAnteriorId by mutableStateOf<Int?>(null)
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

    // DOCUMENTO ESCANEADO (rutas de archivo como texto)
    val paginasEscaneadas = mutableStateListOf<String>()

    var pdfEscaneado by mutableStateOf<String?>(null)
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

    // Espejo en memoria de la tabla "documentos"; se actualiza solo cuando cambia la BD.
    val documentos = mutableStateListOf<Documento>()
    val versionesDocumento = mutableStateListOf<VersionDocumentoEntity>()
    var documentoSeleccionado by mutableStateOf<Documento?>(null)
        private set
    var documentoEnEdicion by mutableStateOf<Documento?>(null)
        private set
    var funcionarioPermitidoId by mutableStateOf<Int?>(null)
        private set
    private var usuarioAutorizado: Usuario? = null
    private var funcionariosPorId: Map<Int, Funcionario> = emptyMap()

    fun restringirAFuncionario(id: Int?) {
        funcionarioPermitidoId = id
    }

    fun establecerAutorizacion(usuario: Usuario?, funcionarios: List<Funcionario>) {
        usuarioAutorizado = usuario
        funcionariosPorId = funcionarios.associateBy { it.id }
        val seleccionado = documentoSeleccionado
        if (seleccionado != null && !AccesoporRol.puedeVerDocumento(
                usuario,
                seleccionado,
                funcionariosPorId[seleccionado.funcionarioId]
            )
        ) {
            documentoSeleccionado = null
        }
    }

    init {
        viewModelScope.launch {
            ultimoId = maxOf(ultimoId, repository.maxId() ?: 0)
        }
        viewModelScope.launch {
            repository.observarTodos().collect { lista ->
                documentos.clear()
                documentos.addAll(lista.map { it.toModel() })
            }
        }
    }

    // MÉTODOS DE CAMBIO GENERALES
    fun cambiarTipo(valor: String) { tipo = valor; errorTipo = null }
    fun cambiarFuncionario(id: Int?) { funcionarioId = id; errorFuncionario = null }
    fun cambiarEstado(valor: String) { estado = valor; errorEstado = null }
    fun cambiarMotivoCambio(valor: String) { motivoCambio = valor; errorMotivoCambio = null }

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

    fun establecerEscaneo(paginas: List<String>, pdf: String?) {
        paginasEscaneadas.clear()
        paginasEscaneadas.addAll(paginas)
        pdfEscaneado = pdf
    }

    fun quitarEscaneo() {
        paginasEscaneadas.clear()
        pdfEscaneado = null
    }

    fun seleccionarDocumento(documento: Documento) {
        if (!AccesoporRol.puedeVerDocumento(
                usuarioAutorizado,
                documento,
                funcionariosPorId[documento.funcionarioId]
            )
        ) return

        documentoSeleccionado = documento
    }

    fun obtenerDocumentosPorFuncionario(idFuncionario: Int): List<Documento> =
        documentos.filter {
            it.funcionarioId == idFuncionario && AccesoporRol.puedeVerDocumento(
                usuarioAutorizado,
                it,
                funcionariosPorId[it.funcionarioId]
            )
        }

    fun documentosVisibles(funcionarioIdPermitido: Int?): List<Documento> =
        documentos.filter { documento ->
            (funcionarioIdPermitido == null || documento.funcionarioId == funcionarioIdPermitido) &&
                AccesoporRol.puedeVerDocumento(
                    usuarioAutorizado,
                    documento,
                    funcionariosPorId[documento.funcionarioId]
                )
        }

    fun cargarVersiones(documentoId: Int) {
        viewModelScope.launch {
            val versiones = versionDao.listarPorDocumento(documentoId)
            versionesDocumento.clear()
            versionesDocumento.addAll(versiones)
        }
    }

    fun prepararRegistroParaFuncionario(idFuncionario: Int) {
        limpiarFormulario()
        funcionarioId = idFuncionario
    }

    fun limpiarFormulario() {
        tipo = ""; funcionarioId = null; estado = ""
        motivoCambio = ""; errorMotivoCambio = null; solicitudAnteriorId = null
        subtipoLaboral = ""
        tipoSolicitud = ""; respaldo = ""; fechaInicio = ""; fechaFin = ""; estadoAprobacion = ""
        nombreCurso = ""; certificadoRespaldo = ""; cantidadHoras = ""; estadoValidacion = ""
        errorTipo = null; errorFuncionario = null; errorEstado = null; mensajeExito = null
        quitarEscaneo()
        documentoEnEdicion = null
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

        val nuevoId = maxOf(ultimoId, documentos.maxOfOrNull { it.id } ?: 0) + 1
        ultimoId = nuevoId

        val nuevoDocumento = Documento(
            id = nuevoId,
            funcionarioId = funcionarioId!!,
            usuarioCargaId = usuarioLogueadoId,
            fechaCarga = LocalDate.now().format(DateTimeFormatter.ISO_DATE),
            estado = estado,
            clasificacion = especificacion,
            paginasEscaneadas = paginasEscaneadas.toList(),
            pdfEscaneado = pdfEscaneado
        )

        // ROOM: la lista de los documentos se guardan aca
        viewModelScope.launch { repository.insertar(nuevoDocumento.toEntity()) }
        documentoSeleccionado = nuevoDocumento
        return true
    }

    fun prepararEdicion(
        documento: Documento,
        valoresIniciales: Documento = documento,
        solicitudAnteriorId: Int? = null
    ) {
        limpiarFormulario()
        documentoEnEdicion = documento
        this.solicitudAnteriorId = solicitudAnteriorId
        funcionarioId = valoresIniciales.funcionarioId
        estado = valoresIniciales.estado

        when (val c = valoresIniciales.clasificacion) {
            is EspecificacionDocumento.Laboral -> {
                tipo = "Laboral"
                subtipoLaboral = c.subtipo
            }
            is EspecificacionDocumento.Permiso -> {
                tipo = "Permiso"
                tipoSolicitud = c.tipoSolicitud
                respaldo = c.respaldo
                fechaInicio = c.fechaInicio
                fechaFin = c.fechaFin
                estadoAprobacion = c.estadoAprobacion
            }
            is EspecificacionDocumento.Capacitacion -> {
                tipo = "Capacitacion"
                nombreCurso = c.nombreCurso
                certificadoRespaldo = c.certificadoRespaldo
                cantidadHoras = c.cantidadHoras.toString()
                estadoValidacion = c.estadoValidacion
            }
            is EspecificacionDocumento.General -> tipo = "General"
        }
        establecerEscaneo(valoresIniciales.paginasEscaneadas, valoresIniciales.pdfEscaneado)
    }

    fun prepararSolicitudCambio(): Pair<Documento, List<String>>? {
        val original = documentoEnEdicion ?: return null
        if (estado.isBlank()) {
            errorEstado = "Requerido"
            return null
        }
        if (motivoCambio.isBlank()) {
            errorMotivoCambio = "Indica el motivo de la solicitud."
            return null
        }

        val actualizado = crearPropuesta(original)
        val cambios = cambiosEntre(original, actualizado)
        if (cambios.isEmpty()) {
            errorMotivoCambio = "Modifica al menos un dato antes de enviar la solicitud."
            return null
        }
        return actualizado to cambios
    }

    fun prepararEdicionDirectaRRHH(): Pair<Documento, List<String>>? {
        val original = documentoEnEdicion ?: return null
        if (estado != "Vigente") {
            errorEstado = "Confirma el estado Vigente para guardar la edición directa."
            return null
        }
        val actualizado = crearPropuesta(original)
        val cambios = cambiosEntre(original, actualizado)
        if (cambios.isEmpty()) {
            errorEstado = "Modifica algún dato antes de guardar."
            return null
        }
        return actualizado to cambios
    }

    fun guardarEdicionDirectaRRHH(
        usuario: Usuario,
        propuesta: Documento,
        camposAfectados: List<String>,
        alCompletar: (Boolean) -> Unit
    ) {
        val original = documentoEnEdicion
        if (usuario.rol != RolUsuario.RECURSOS_HUMANOS || original == null ||
            original.id != propuesta.id || propuesta.estado != "Vigente"
        ) {
            alCompletar(false)
            return
        }
        viewModelScope.launch {
            runCatching {
                solicitudCambioRepository.actualizarDirectamentePorRecursosHumanos(
                    documentoId = original.id,
                    usuarioId = usuario.id,
                    propuesta = propuesta,
                    camposAfectados = camposAfectados
                )
            }.onSuccess { guardado ->
                if (guardado) {
                    documentoSeleccionado = propuesta
                    documentoEnEdicion = null
                }
                alCompletar(guardado)
            }.onFailure {
                alCompletar(false)
            }
        }
    }

    private fun crearPropuesta(original: Documento): Documento {
        val especificacion = when (tipo) {
            "Laboral" -> EspecificacionDocumento.Laboral(subtipoLaboral)
            "Permiso" -> EspecificacionDocumento.Permiso(
                tipoSolicitud, respaldo, fechaInicio, fechaFin, estadoAprobacion
            )
            "Capacitacion" -> EspecificacionDocumento.Capacitacion(
                nombreCurso, certificadoRespaldo,
                cantidadHoras.toIntOrNull() ?: 0, estadoValidacion
            )
            else -> EspecificacionDocumento.General
        }
        return original.copy(
            estado = estado,
            clasificacion = especificacion,
            paginasEscaneadas = paginasEscaneadas.toList(),
            pdfEscaneado = pdfEscaneado
        )
    }

    private fun cambiosEntre(antes: Documento, despues: Documento): List<String> = buildList {
        if (antes.estado != despues.estado) add("estado (${antes.estado} → ${despues.estado})")
        if (antes.clasificacion != despues.clasificacion) add("datos del documento")
        if (antes.paginasEscaneadas != despues.paginasEscaneadas || antes.pdfEscaneado != despues.pdfEscaneado) {
            add("archivo escaneado")
        }
    }
}
