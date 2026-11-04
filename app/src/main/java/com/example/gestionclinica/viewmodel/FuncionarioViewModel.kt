package com.example.gestionclinica.viewmodel

import android.app.Application
import android.database.sqlite.SQLiteConstraintException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionclinica.data.AppDatabase
import com.example.gestionclinica.model.Funcionario
import kotlinx.coroutines.launch

class FuncionarioViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).funcionarioDao()

    // Texto ingresado en el buscador
    var busqueda by mutableStateOf("")
        private set

    // Funcionario seleccionado para consultar su expediente
    var funcionarioSeleccionado by mutableStateOf<Funcionario?>(null)
        private set

    // Espejo en memoria de la tabla "funcionarios"; se actualiza solo cuando cambia la BD.
    private val funcionarios = mutableStateListOf<Funcionario>()

    init {
        viewModelScope.launch {
            dao.observarTodos().collect { lista ->
                funcionarios.clear()
                funcionarios.addAll(lista)
            }
        }
    }

    // Lista que utiliza la pantalla
    val funcionariosFiltrados: List<Funcionario>
        get() {
            if (busqueda.isBlank()) {
                return funcionarios.toList()
            }
            return funcionarios.filter { funcionario ->
                funcionario.nombre.contains(busqueda, ignoreCase = true) ||
                        funcionario.rut.contains(busqueda, ignoreCase = true) ||
                        funcionario.cargo.contains(busqueda, ignoreCase = true)
            }
        }

    fun cambiarBusqueda(valor: String) {
        busqueda = valor
    }

    fun seleccionarFuncionario(funcionario: Funcionario) {
        funcionarioSeleccionado = funcionario
    }

    fun limpiarBusqueda() {
        busqueda = ""
    }

    // ---------- FORMULARIO: NUEVO FUNCIONARIO ----------
    var nuevoRut by mutableStateOf("")
        private set
    var nuevoNombre by mutableStateOf("")
        private set
    var nuevoCentroSalud by mutableStateOf("")
        private set
    var nuevoCargo by mutableStateOf("")
        private set
    var nuevaProfesion by mutableStateOf("")
        private set
    var nuevoTipoContrato by mutableStateOf("Planta")
        private set
    var nuevaFechaIngreso by mutableStateOf("")
        private set
    var nuevaSituacion by mutableStateOf("Vigente")
        private set
    var nuevoEstado by mutableStateOf("Activo")
        private set

    var errorRut by mutableStateOf<String?>(null)
        private set
    var errorNombre by mutableStateOf<String?>(null)
        private set
    var errorCentroSalud by mutableStateOf<String?>(null)
        private set
    var errorCargo by mutableStateOf<String?>(null)
        private set
    var errorFecha by mutableStateOf<String?>(null)
        private set

    fun cambiarNuevoRut(valor: String) { nuevoRut = valor; errorRut = null }
    fun cambiarNuevoNombre(valor: String) { nuevoNombre = valor; errorNombre = null }
    fun cambiarNuevoCentroSalud(valor: String) { nuevoCentroSalud = valor; errorCentroSalud = null }
    fun cambiarNuevoCargo(valor: String) { nuevoCargo = valor; errorCargo = null }
    fun cambiarNuevaProfesion(valor: String) { nuevaProfesion = valor }
    fun cambiarNuevoTipoContrato(valor: String) { nuevoTipoContrato = valor }
    fun cambiarNuevaFechaIngreso(valor: String) { nuevaFechaIngreso = valor; errorFecha = null }
    fun cambiarNuevaSituacion(valor: String) { nuevaSituacion = valor }
    fun cambiarNuevoEstado(valor: String) { nuevoEstado = valor }

    fun limpiarFormularioNuevo() {
        nuevoRut = ""; nuevoNombre = ""; nuevoCentroSalud = ""; nuevoCargo = ""
        nuevaProfesion = ""; nuevoTipoContrato = "Planta"; nuevaFechaIngreso = ""
        nuevaSituacion = "Vigente"; nuevoEstado = "Activo"
        errorRut = null; errorNombre = null; errorCentroSalud = null
        errorCargo = null; errorFecha = null
    }

    /** Valida y agrega el funcionario. Devuelve true si se guardó. */
    fun guardarFuncionario(): Boolean {
        val rut = nuevoRut.trim()
        val fecha = nuevaFechaIngreso.trim()

        errorRut = when {
            rut.isBlank() -> "Requerido"
            !formatoRut.matches(rut) -> "Formato: 12.345.678-9"
            funcionarios.any { normalizarRut(it.rut) == normalizarRut(rut) } ->
                "Ya existe un funcionario con este RUT"
            else -> null
        }
        errorNombre = if (nuevoNombre.isBlank()) "Requerido" else null
        errorCentroSalud = if (nuevoCentroSalud.isBlank()) "Requerido" else null
        errorCargo = if (nuevoCargo.isBlank()) "Requerido" else null
        errorFecha = if (fecha.isNotEmpty() && !formatoFecha.matches(fecha)) "Formato: YYYY-MM-DD" else null

        if (listOf(errorRut, errorNombre, errorCentroSalud, errorCargo, errorFecha).any { it != null }) {
            return false
        }

        val nuevo = Funcionario(
            id = 0, // lo asigna Room (autoincremental)
            rut = rut,
            nombre = nuevoNombre.trim(),
            centroSalud = nuevoCentroSalud.trim(),
            cargo = nuevoCargo.trim(),
            profesion = nuevaProfesion.trim(),
            tipoContrato = nuevoTipoContrato,
            fechaIngreso = fecha,
            situacionContractual = nuevaSituacion,
            estado = nuevoEstado
        )
        viewModelScope.launch {
            try {
                dao.insertar(nuevo)
            } catch (e: SQLiteConstraintException) {
                // RUT duplicado: ya se valida arriba; el índice único es la última barrera.
            }
        }
        limpiarFormularioNuevo()
        return true
    }

    private val formatoRut = Regex("^\\d{1,2}(\\.?\\d{3}){2}-[\\dkK]$")
    private val formatoFecha = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    private fun normalizarRut(rut: String): String = rut.replace(".", "").uppercase()
}