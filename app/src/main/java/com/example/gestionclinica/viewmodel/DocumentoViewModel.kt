package com.example.gestionclinica.viewmodel


import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestionclinica.model.Documento

class DocumentoViewModel : ViewModel() {

    // Campos del formulario
    var nombre by mutableStateOf("")
        private set

    var tipo by mutableStateOf("")
        private set

    var funcionario by mutableStateOf("")
        private set

    var estado by mutableStateOf("")
        private set

    // Errores
    var errorNombre by mutableStateOf<String?>(null)
        private set

    var errorTipo by mutableStateOf<String?>(null)
        private set

    var errorFuncionario by mutableStateOf<String?>(null)
        private set

    var errorEstado by mutableStateOf<String?>(null)
        private set

    // Mensaje de éxito
    var mensajeExito by mutableStateOf<String?>(null)
        private set

    // Lista de documentos registrados
    val documentos = mutableStateListOf<Documento>()

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

    fun guardarDocumento(): Boolean {

        // Limpiar errores anteriores
        errorNombre = null
        errorTipo = null
        errorFuncionario = null
        errorEstado = null
        mensajeExito = null

        var formularioValido = true

        // Validar nombre
        if (nombre.isBlank()) {
            errorNombre = "El nombre del documento es obligatorio"
            formularioValido = false
        } else if (nombre.length < 3) {
            errorNombre = "El nombre debe tener al menos 3 caracteres"
            formularioValido = false
        }

        // Validar tipo
        if (tipo.isBlank()) {
            errorTipo = "Debe seleccionar un tipo de documento"
            formularioValido = false
        }

        // Validar funcionario
        if (funcionario.isBlank()) {
            errorFuncionario = "El funcionario es obligatorio"
            formularioValido = false
        }

        // Validar estado
        if (estado.isBlank()) {
            errorEstado = "Debe seleccionar un estado"
            formularioValido = false
        }

        if (!formularioValido) {
            return false
        }

        // Crear el documento
        val nuevoDocumento = Documento(
            id = documentos.size + 1,
            nombre = nombre,
            tipo = tipo,
            funcionario = funcionario,
            estado = estado
        )

        // Guardarlo en la lista
        documentos.add(nuevoDocumento)

        mensajeExito = "Documento registrado correctamente"

        return true
    }
}