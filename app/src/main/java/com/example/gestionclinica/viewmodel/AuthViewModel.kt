package com.example.gestionclinica.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.model.Usuario

class AuthViewModel : ViewModel() {
    var rut by mutableStateOf("")
        private set
    var password by mutableStateOf("")
        private set
    var errorLogin by mutableStateOf<String?>(null)
        private set
    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // Usuarios simulados (quemados) para probar el inicio de sesión
    private val usuariosBd = listOf(
        Usuario(id = 1, nombre = "Admin RRHH", rut = "11-1", rol = RolUsuario.RECURSOS_HUMANOS, activo = true),
        Usuario(id = 2, nombre = "Funcionario Básico", rut = "22-2", rol = RolUsuario.FUNCIONARIO, activo = true),
        Usuario(id = 3, nombre = "Jefe Médico", rut = "33-3", rol = RolUsuario.JEFATURA, activo = true)
    )

    fun cambiarRut(valor: String) { rut = valor; errorLogin = null }
    fun cambiarPassword(valor: String) { password = valor; errorLogin = null }

    fun login(): Boolean {
        // Para simplificar ahora mismo, la contraseña será "123" para todos
        val user = usuariosBd.find { it.rut == rut }

        if (user != null && password == "123") {
            if (!user.activo) {
                errorLogin = "Usuario inactivo"
                return false
            }
            usuarioActual = user
            return true
        }

        errorLogin = "RUT o contraseña incorrectos"
        return false
    }

    fun logout() {
        usuarioActual = null
        rut = ""
        password = ""
    }
}