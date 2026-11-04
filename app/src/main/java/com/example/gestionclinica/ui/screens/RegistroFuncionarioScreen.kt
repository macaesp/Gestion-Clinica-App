package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.ui.components.DropdownField
import com.example.gestionclinica.ui.components.FormField
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.SectionCard
import com.example.gestionclinica.viewmodel.FuncionarioViewModel

private val tiposContrato = listOf("Planta", "Contrata", "Honorarios", "Reemplazo")
private val situacionesContractuales = listOf("Vigente", "Pendiente", "Vencido")
private val estadosFuncionario = listOf("Activo", "Inactivo")

@Composable
fun RegistroFuncionarioScreen(
    viewModel: FuncionarioViewModel,
    onGuardado: () -> Unit,
    onVolver: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        ScreenHeader(
            title = "Nuevo funcionario",
            subtitle = "Completa los datos del funcionario",
            onBack = onVolver
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Datos personales") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        value = viewModel.nuevoRut,
                        onValueChange = viewModel::cambiarNuevoRut,
                        label = "RUT",
                        placeholder = "12.345.678-9",
                        error = viewModel.errorRut
                    )
                    FormField(
                        value = viewModel.nuevoNombre,
                        onValueChange = viewModel::cambiarNuevoNombre,
                        label = "Nombre completo",
                        error = viewModel.errorNombre
                    )
                }
            }

            SectionCard(title = "Datos laborales") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    FormField(
                        value = viewModel.nuevoCentroSalud,
                        onValueChange = viewModel::cambiarNuevoCentroSalud,
                        label = "Centro de salud",
                        placeholder = "Ej: CESFAM",
                        error = viewModel.errorCentroSalud
                    )
                    FormField(
                        value = viewModel.nuevoCargo,
                        onValueChange = viewModel::cambiarNuevoCargo,
                        label = "Cargo",
                        error = viewModel.errorCargo
                    )
                    FormField(
                        value = viewModel.nuevaProfesion,
                        onValueChange = viewModel::cambiarNuevaProfesion,
                        label = "Profesión"
                    )
                    FormField(
                        value = viewModel.nuevaFechaIngreso,
                        onValueChange = viewModel::cambiarNuevaFechaIngreso,
                        label = "Fecha de ingreso",
                        placeholder = "YYYY-MM-DD",
                        error = viewModel.errorFecha
                    )
                }
            }

            SectionCard(title = "Situación contractual") {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    DropdownField(
                        value = viewModel.nuevoTipoContrato,
                        label = "Tipo de contrato",
                        options = tiposContrato,
                        onSelect = viewModel::cambiarNuevoTipoContrato
                    )
                    DropdownField(
                        value = viewModel.nuevaSituacion,
                        label = "Situación contractual",
                        options = situacionesContractuales,
                        onSelect = viewModel::cambiarNuevaSituacion
                    )
                    DropdownField(
                        value = viewModel.nuevoEstado,
                        label = "Estado",
                        options = estadosFuncionario,
                        onSelect = viewModel::cambiarNuevoEstado
                    )
                }
            }

            Button(
                onClick = { if (viewModel.guardarFuncionario()) onGuardado() },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Guardar funcionario")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}