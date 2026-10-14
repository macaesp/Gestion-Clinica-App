package com.example.gestionclinica.viewmodel

import androidx.compose.runtime.getValue
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

    // Datos temporales adaptados al nuevo modelo
    private val funcionarios = listOf(
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
                return funcionarios
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
}