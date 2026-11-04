package com.example.gestionclinica.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionclinica.data.AppDatabase
import com.example.gestionclinica.data.PasswordHasher
import com.example.gestionclinica.data.toModel
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.model.Usuario
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).usuarioDao()

    var rut by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorLogin by mutableStateOf<String?>(null)
        private set

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // true mientras se verifica la contraseña (evita doble toque en "Ingresar")
    var cargando by mutableStateOf(false)
        private set

    fun cambiarRut(valor: String) {
        rut = valor
        errorLogin = null
    }

    fun cambiarPassword(valor: String) {
        password = valor
        errorLogin = null
    }

    /**
     * Verifica RUT y contraseña contra la BD. Si todo es correcto guarda el
     * usuario actual y llama a [onExito].
     */
    fun login(tipoAcceso: String, onExito: () -> Unit) {
        if (cargando) return
        val rutBuscado = rut.replace(".", "").trim().uppercase()
        val claveIngresada = password

        viewModelScope.launch {
            cargando = true

            val entidad = dao.buscarPorRut(rutBuscado)
            val credencialesOk = withContext(Dispatchers.Default) {
                if (entidad != null) {
                    PasswordHasher.verificar(claveIngresada, entidad.salt, entidad.passwordHash)
                } else {
                    // Misma carga de trabajo aunque el RUT no exista (no revela qué RUT existen)
                    PasswordHasher.hash(claveIngresada, SAL_FALSA)
                    false
                }
            }

            cargando = false

            if (entidad == null || !credencialesOk) {
                errorLogin = "RUT o contraseña incorrectos"
                return@launch
            }

            val usuario = entidad.toModel()

            if (!usuario.activo) {
                errorLogin = "Usuario inactivo"
                return@launch
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
                return@launch
            }

            usuarioActual = usuario
            errorLogin = null
            password = ""
            onExito()
        }
    }

    fun logout() {
        usuarioActual = null
        rut = ""
        password = ""
        errorLogin = null
    }

    private companion object {
        const val SAL_FALSA = "AAAAAAAAAAAAAAAAAAAAAA=="
    }
}