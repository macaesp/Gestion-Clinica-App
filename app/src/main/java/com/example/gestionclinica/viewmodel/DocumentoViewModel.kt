package com.example.gestionclinica.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

class DocumentoViewModel: ViewModel() {
    var nombre by mutableStateOf("")
        private set

    var tipo by mutableStateOf("")
        private set

    var funcionario by mutableStateOf("")
        private set

    var estado by mutableStateOf("")
        private set

    var errorNombre by mutableStateOf<String?>(null)
        private set

    var mensajeExito by mutableStateOf<String?>(null)
        private set

    fun cambiarNombre(valor: String){
        nombre = valor
        errorNombre = null
    }

    fun cambiarTipo(valor: String){
        tipo = valor
    }

    fun cambiarFuncionario(valor: String){
        funcionario = valor
    }

    fun cambiarEstado(valor: String){
        estado = valor
    }

    fun guardarDocumento(): Boolean{
        // validar el nombre del documento
        if (nombre.isBlank()){
            errorNombre = "El nombre del documento es obligatorio"
            mensajeExito = null
            return false
        }

        // validacion que el texto tiene que ser mas de 3 caracteres
        if (nombre.length < 3){
            errorNombre = "El nombre debe tener al menos 3 caracteres"
            mensajeExito = null
            return false
        }

        errorNombre = null
        mensajeExito = "Documento registrado correctamente"

        return true
    }
}