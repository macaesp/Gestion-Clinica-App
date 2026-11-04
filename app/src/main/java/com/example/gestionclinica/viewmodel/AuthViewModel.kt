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
    private val funcionarioDao = AppDatabase.getInstance(application).funcionarioDao()

    var rut by mutableStateOf("")
        private set

    var password by mutableStateOf("")
        private set

    var errorLogin by mutableStateOf<String?>(null)
        private set

    var usuarioActual by mutableStateOf<Usuario?>(null)
        private set

    // ID del expediente vinculado al usuario con rol FUNCIONARIO.
    var funcionarioActualId by mutableStateOf<Int?>(null)
        private set

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

    fun login(tipoAcceso: String, onExito: () -> Unit) {
        if (cargando) return

        val rutBuscado = normalizarRut(rut)
        val claveIngresada = password

        viewModelScope.launch {
            cargando = true

            val entidad = dao.buscarPorRut(rutBuscado)

            val credencialesOk = withContext(Dispatchers.Default) {
                if (entidad != null) {
                    PasswordHasher.verificar(
                        claveIngresada,
                        entidad.salt,
                        entidad.passwordHash
                    )
                } else {
                    // Mantiene un tiempo de respuesta similar si el RUT no existe.
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

            // Cada funcionario se vincula a su expediente mediante el RUT.
            val idFuncionario = if (usuario.rol == RolUsuario.FUNCIONARIO) {
                funcionarioDao.buscarPorRutNormalizado(
                    normalizarRut(usuario.rut)
                )?.id
            } else {
                null
            }

            if (usuario.rol == RolUsuario.FUNCIONARIO && idFuncionario == null) {
                usuarioActual = null
                funcionarioActualId = null
                errorLogin =
                    "No existe un expediente asociado a este RUT. Contacta a Recursos Humanos."
                return@launch
            }

            usuarioActual = usuario
            funcionarioActualId = idFuncionario
            errorLogin = null
            password = ""

            onExito()
        }
    }

    fun logout() {
        usuarioActual = null
        funcionarioActualId = null
        rut = ""
        password = ""
        errorLogin = null
    }

    private fun normalizarRut(valor: String): String =
        valor.replace(".", "").trim().uppercase()

    private companion object {
        const val SAL_FALSA = "AAAAAAAAAAAAAAAAAAAAAA=="
    }
}