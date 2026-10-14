package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.viewmodel.FuncionarioViewModel

@Composable
fun ExpedienteScreen(
    viewModel: FuncionarioViewModel,
    onVerDocumentos: () -> Unit,
    onRegistrarDocumento: () -> Unit,
    onVolver: () -> Unit
) {
    val funcionario = viewModel.funcionarioSeleccionado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp, vertical = 24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Column {
                Text(text = "Expediente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Text(text = "CMQ Área Salud", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))

        if (funcionario == null) {
            Text("No se ha seleccionado un funcionario.", style = MaterialTheme.typography.bodyLarge)
            return@Column
        }

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null)
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = funcionario.nombre, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                Text(text = "RUT", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = funcionario.rut, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Profesión / Cargo", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${funcionario.profesion} - ${funcionario.cargo}", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Centro de Salud", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = funcionario.centroSalud, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Contrato", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = "${funcionario.tipoContrato} (${funcionario.situacionContractual})", style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(8.dp))

                Text(text = "Fecha de Ingreso", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = funcionario.fechaIngreso, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(12.dp))

                Text(text = "Estado", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(text = funcionario.estado, style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
            }
        }
        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "Documentación", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Consulta o registra documentación asociada a este funcionario.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = onVerDocumentos, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Default.Description, contentDescription = null)
            Spacer(modifier = Modifier.padding(4.dp))
            Text("Ver documentos")
        }
        Spacer(modifier = Modifier.height(12.dp))
        Button(onClick = onRegistrarDocumento, modifier = Modifier.fillMaxWidth()) {
            Icon(imageVector = Icons.Default.Description, contentDescription = null)
            Spacer(modifier = Modifier.padding(4.dp))
            Text("Registrar documento")
        }
    }
}