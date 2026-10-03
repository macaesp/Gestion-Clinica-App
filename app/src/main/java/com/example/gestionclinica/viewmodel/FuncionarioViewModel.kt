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

    // Datos temporales mientras todavía no utilizamos una base de datos
    private val funcionarios = listOf(
        Funcionario(
            id = 1,
            nombre = "María González",
            rut = "12.345.678-9",
            cargo = "Enfermera",
            establecimiento = "CESFAM",
            estado = "Activo"
        ),
        Funcionario(
            id = 2,
            nombre = "Juan Pérez",
            rut = "15.678.901-2",
            cargo = "Técnico en Enfermería",
            establecimiento = "CECOSF",
            estado = "Activo"
        ),
        Funcionario(
            id = 3,
            nombre = "Carolina Soto",
            rut = "17.234.567-8",
            cargo = "Administrativa",
            establecimiento = "CESFAM",
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

                funcionario.nombre.contains(
                    busqueda,
                    ignoreCase = true
                ) ||
                        funcionario.rut.contains(
                            busqueda,
                            ignoreCase = true
                        ) ||
                        funcionario.cargo.contains(
                            busqueda,
                            ignoreCase = true
                        )
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