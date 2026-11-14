package com.example.gestionclinica.viewmodel

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionclinica.data.AppDatabase
import com.example.gestionclinica.data.PasswordHasher
import com.example.gestionclinica.data.UsuarioEntity
import com.example.gestionclinica.data.toModel
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.model.Usuario
import com.example.gestionclinica.security.ValidacionCuenta
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    var funcionarioActualId by mutableStateOf<Int?>(null)
        private set

    var cargando by mutableStateOf(false)
        private set

    private val _usuarios = MutableStateFlow<List<Usuario>>(emptyList())
    val usuarios: StateFlow<List<Usuario>> = _usuarios.asStateFlow()

    private val _centrosDisponibles = MutableStateFlow<List<String>>(emptyList())
    val centrosDisponibles: StateFlow<List<String>> = _centrosDisponibles.asStateFlow()

    var mensajeGestionUsuarios by mutableStateOf<String?>(null)
        private set

    init {
        viewModelScope.launch {
            funcionarioDao.observarTodos().collect { funcionarios ->
                _centrosDisponibles.value = funcionarios
                    .map { it.centroSalud.trim() }
                    .filter { it.isNotBlank() }
                    .distinctBy { it.uppercase() }
                    .sorted()
            }
        }
    }

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

            try {
                val entidad = dao.buscarPorRut(rutBuscado)

                val credencialesOk = withContext(Dispatchers.Default) {
                    if (entidad != null) {
                        PasswordHasher.verificar(
                            claveIngresada,
                            entidad.salt,
                            entidad.passwordHash
                        )
                    } else {
                        PasswordHasher.hash(claveIngresada, SAL_FALSA)
                        false
                    }
                }

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
                    RolUsuario.JEFATURA
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

                val idFuncionario =
                    if (usuario.rol == RolUsuario.FUNCIONARIO) {
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
            } catch (_: Exception) {
                errorLogin = "No se pudo iniciar sesión. Intenta nuevamente."
            } finally {
                cargando = false
            }
        }
    }

    fun cargarUsuarios() {
        viewModelScope.launch {
            try {
                _usuarios.value = dao.listarTodos().map { it.toModel() }
            } catch (_: Exception) {
                mensajeGestionUsuarios = "No se pudieron cargar las cuentas."
            }
        }
    }

    fun crearUsuario(
        nombre: String,
        rut: String,
        rol: RolUsuario,
        claveInicial: String,
        centroAsignado: String?,
        onResultado: (String?) -> Unit
    ) {
        val nombreLimpio = nombre.trim()
        val rutLimpio = normalizarRut(rut)
        val clave = claveInicial

        val errorValidacion = ValidacionCuenta.validar(
            nombre = nombreLimpio,
            rutNormalizado = rutLimpio,
            claveInicial = clave,
            rol = rol,
            centroAsignado = centroAsignado
        )
        if (errorValidacion != null) {
            onResultado(errorValidacion)
            return
        }

        viewModelScope.launch {
            try {
                if (dao.buscarPorRut(rutLimpio) != null) {
                    onResultado("Ya existe una cuenta con ese RUT.")
                    return@launch
                }

                val sal = PasswordHasher.nuevaSal()
                val hash = withContext(Dispatchers.Default) {
                    PasswordHasher.hash(clave, sal)
                }

                dao.insertar(
                    UsuarioEntity(
                        id = 0,
                        nombre = nombreLimpio,
                        rut = rutLimpio,
                        rol = rol.name,
                        activo = true,
                        passwordHash = hash,
                        salt = sal,
                        centroAsignado = centroAsignado
                    )
                )

                cargarUsuarios()
                onResultado(null)
            } catch (_: Exception) {
                onResultado("No se pudo crear la cuenta. Revisa que el RUT no esté repetido.")
            }
        }
    }

    fun asignarCentroUsuario(id: Int, centro: String?) {
        viewModelScope.launch {
            try {
                dao.actualizarCentro(id, centro)
                cargarUsuarios()
                mensajeGestionUsuarios = "Centro asignado actualizado."
            } catch (_: Exception) {
                mensajeGestionUsuarios = "No se pudo actualizar el centro asignado."
            }
        }
    }

    fun cambiarEstadoUsuario(id: Int, activo: Boolean) {
        if (id == usuarioActual?.id && !activo) {
            mensajeGestionUsuarios =
                "No puedes desactivar tu propia cuenta mientras tienes sesión iniciada."
            return
        }

        viewModelScope.launch {
            try {
                val filasActualizadas = dao.actualizarEstado(id, activo)

                if (filasActualizadas > 0) {
                    cargarUsuarios()
                    mensajeGestionUsuarios =
                        if (activo) "Cuenta activada." else "Cuenta desactivada."
                } else {
                    mensajeGestionUsuarios = "No se encontró la cuenta."
                }
            } catch (_: Exception) {
                mensajeGestionUsuarios = "No se pudo actualizar el estado de la cuenta."
            }
        }
    }

    fun limpiarMensajeGestionUsuarios() {
        mensajeGestionUsuarios = null
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
