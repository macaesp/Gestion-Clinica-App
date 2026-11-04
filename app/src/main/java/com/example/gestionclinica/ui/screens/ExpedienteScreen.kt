package com.example.gestionclinica.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.ui.components.EmptyState
import com.example.gestionclinica.ui.components.InfoRow
import com.example.gestionclinica.ui.components.InitialsAvatar
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.SectionCard
import com.example.gestionclinica.ui.components.StatusChip
import com.example.gestionclinica.viewmodel.FuncionarioViewModel

@Composable
fun ExpedienteScreen(
    viewModel: FuncionarioViewModel,
    onVerDocumentos: () -> Unit,
    onRegistrarDocumento: () -> Unit,
    onVolver: () -> Unit
) {
    val funcionario = viewModel.funcionarioSeleccionado

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Expediente",
            subtitle = "CMQ Área Salud",
            onBack = onVolver
        )

        if (funcionario == null) {
            EmptyState(
                icon = Icons.Default.Person,
                title = "Sin funcionario",
                message = "No se ha seleccionado un funcionario."
            )
            return@Column
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Perfil
            SectionCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    InitialsAvatar(name = funcionario.nombre, size = 72.dp)
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = funcionario.nombre,
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${funcionario.profesion} · ${funcionario.cargo}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    StatusChip(text = funcionario.estado)
                }
            }

            SectionCard(title = "Datos del funcionario") {
                InfoRow("RUT", funcionario.rut)
                InfoRow("Centro de salud", funcionario.centroSalud)
                InfoRow("Fecha de ingreso", funcionario.fechaIngreso)
            }

            SectionCard(title = "Situación contractual") {
                InfoRow("Tipo de contrato", funcionario.tipoContrato)
                InfoRow("Situación", funcionario.situacionContractual)
            }

            Text(
                text = "Documentación",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground
            )

            Button(
                onClick = onVerDocumentos,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Description, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Ver documentos")
            }
            OutlinedButton(
                onClick = onRegistrarDocumento,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Registrar documento")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}