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

    // Cuentas de prueba locales. La contraseña de todas es 123.
    private val usuariosBd = listOf(
        Usuario(
            id = 1,
            nombre = "Admin RRHH",
            rut = "11-1",
            rol = RolUsuario.RECURSOS_HUMANOS,
            activo = true
        ),
        Usuario(
            id = 2,
            nombre = "Funcionario Básico",
            rut = "22-2",
            rol = RolUsuario.FUNCIONARIO,
            activo = true
        ),
        Usuario(
            id = 3,
            nombre = "Jefe Médico",
            rut = "33-3",
            rol = RolUsuario.JEFATURA,
            activo = true
        )
    )

    fun cambiarRut(valor: String) {
        rut = valor
        errorLogin = null
    }

    fun cambiarPassword(valor: String) {
        password = valor
        errorLogin = null
    }

    fun login(tipoAcceso: String): Boolean {
        val usuario = usuariosBd.find { it.rut == rut }

        if (usuario == null || password != "123") {
            errorLogin = "RUT o contraseña incorrectos"
            return false
        }

        if (!usuario.activo) {
            errorLogin = "Usuario inactivo"
            return false
        }

        val esAdmin = usuario.rol in setOf(
            RolUsuario.ADMINISTRADOR_SISTEMA,
            RolUsuario.RECURSOS_HUMANOS,
            RolUsuario.JEFATURA,
            RolUsuario.TI_CIBERSEGURIDAD
        )

        val accesoCorrecto = when (tipoAcceso) {
            "Admin" -> esAdmin
            "Usuario" -> !esAdmin
            else -> false
        }

        if (!accesoCorrecto) {
            errorLogin = "La cuenta no corresponde al acceso seleccionado"
            return false
        }

        usuarioActual = usuario
        errorLogin = null
        return true
    }

    fun logout() {
        usuarioActual = null
        rut = ""
        password = ""
        errorLogin = null
    }
}