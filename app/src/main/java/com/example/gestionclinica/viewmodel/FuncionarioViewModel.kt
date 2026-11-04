package com.example.gestionclinica.viewmodel
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.gestionclinica.model.Funcionario

class FuncionarioViewModel : ViewModel() {
    // Texto ingresado en el buscador
    var busqueda by mutableStateOf("")
        private set

    // Funcionario seleccionado para consultar su expediente
    var funcionarioSeleccionado by mutableStateOf<Funcionario?>(null)
        private set

    // Datos temporales (ahora es una lista observable para poder agregar funcionarios)
    private val funcionarios = mutableStateListOf(
        Funcionario(
            id = 1,
            rut = "12.345.678-9",
            nombre = "María González",
            centroSalud = "CESFAM",
            cargo = "Enfermera",
            profesion = "Enfermería",
            tipoContrato = "Planta",
            fechaIngreso = "2015-03-01",
            situacionContractual = "Vigente",
            estado = "Activo"
        ),
        Funcionario(
            id = 2,
            rut = "15.678.901-2",
            nombre = "Juan Pérez",
            centroSalud = "CECOSF",
            cargo = "Técnico en Enfermería",
            profesion = "TENS",
            tipoContrato = "Contrata",
            fechaIngreso = "2018-06-15",
            situacionContractual = "Vigente",
            estado = "Activo"
        )
    )

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
            id = (funcionarios.maxOfOrNull { it.id } ?: 0) + 1,
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
        funcionarios.add(nuevo)
        limpiarFormularioNuevo()
        return true
    }

    private val formatoRut = Regex("^\\d{1,2}(\\.?\\d{3}){2}-[\\dkK]$")
    private val formatoFecha = Regex("^\\d{4}-\\d{2}-\\d{2}$")

    private fun normalizarRut(rut: String): String = rut.replace(".", "").uppercase()
}