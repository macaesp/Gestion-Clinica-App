package com.example.gestionclinica.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestionclinica.model.Documento

class DocumentoViewModel : ViewModel() {

    // CAMPOS DEL FORMULARIO
    var nombre by mutableStateOf("")
        private set

    var tipo by mutableStateOf("")
        private set

    var funcionario by mutableStateOf("")
        private set

    var estado by mutableStateOf("")
        private set

    // ERRORES
    var errorNombre by mutableStateOf<String?>(null)
        private set

    var errorTipo by mutableStateOf<String?>(null)
        private set

    var errorFuncionario by mutableStateOf<String?>(null)
        private set

    var errorEstado by mutableStateOf<String?>(null)
        private set

    var mensajeExito by mutableStateOf<String?>(null)
        private set

    // DOCUMENTOS REGISTRADOS
    val documentos = mutableStateListOf<Documento>()

    // Documento que queremos mostrar en Detalle
    var documentoSeleccionado by mutableStateOf<Documento?>(null)
        private set

    // CAMBIOS DEL FORMULARIO
    fun cambiarNombre(valor: String) {
        nombre = valor
        errorNombre = null
        mensajeExito = null
    }

    fun cambiarTipo(valor: String) {
        tipo = valor
        errorTipo = null
        mensajeExito = null
    }

    fun cambiarFuncionario(valor: String) {
        funcionario = valor
        errorFuncionario = null
        mensajeExito = null
    }

    fun cambiarEstado(valor: String) {
        estado = valor
        errorEstado = null
        mensajeExito = null
    }

    // SELECCIONAR DOCUMENTO
    fun seleccionarDocumento(documento: Documento) {
        documentoSeleccionado = documento
    }

    // DOCUMENTOS POR FUNCIONARIO
    fun obtenerDocumentosPorFuncionario(
        nombreFuncionario: String
    ): List<Documento> {

        return documentos.filter { documento ->
            documento.funcionario.equals(
                nombreFuncionario,
                ignoreCase = true
            )
        }
    }

    // PREPARAR REGISTRO DESDE
    // EL EXPEDIENTE
    fun prepararRegistroParaFuncionario(
        nombreFuncionario: String
    ) {
        funcionario = nombreFuncionario

        nombre = ""
        tipo = ""
        estado = ""

        errorNombre = null
        errorTipo = null
        errorFuncionario = null
        errorEstado = null

        mensajeExito = null
    }

    // LIMPIAR FORMULARIO
    fun limpiarFormulario() {
        nombre = ""
        tipo = ""
        funcionario = ""
        estado = ""

        errorNombre = null
        errorTipo = null
        errorFuncionario = null
        errorEstado = null

        mensajeExito = null
    }

    // GUARDAR DOCUMENTO
    fun guardarDocumento(): Boolean {

        errorNombre = null
        errorTipo = null
        errorFuncionario = null
        errorEstado = null
        mensajeExito = null

        var formularioValido = true

        // VALIDAR NOMBRE
        if (nombre.isBlank()) {

            errorNombre =
                "El nombre del documento es obligatorio"

            formularioValido = false

        } else if (nombre.length < 3) {

            errorNombre =
                "El nombre debe tener al menos 3 caracteres"

            formularioValido = false
        }

        // VALIDAR TIPO
        if (tipo.isBlank()) {

            errorTipo =
                "Debe seleccionar un tipo de documento"

            formularioValido = false
        }

        // VALIDAR FUNCIONARIO
        if (funcionario.isBlank()) {

            errorFuncionario =
                "El funcionario es obligatorio"

            formularioValido = false
        }

        // VALIDAR ESTADO
        if (estado.isBlank()) {

            errorEstado =
                "Debe seleccionar un estado"

            formularioValido = false
        }

        if (!formularioValido) {
            return false
        }

        // CREAR DOCUMENTO
        val nuevoDocumento = Documento(
            id = documentos.size + 1,
            nombre = nombre,
            tipo = tipo,
            funcionario = funcionario,
            estado = estado
        )

        // GUARDAR
        documentos.add(nuevoDocumento)

        // El documento recién creado pasa
        // a ser el documento seleccionado.
        documentoSeleccionado = nuevoDocumento

        mensajeExito =
            "Documento registrado correctamente"

        return true
    }
}