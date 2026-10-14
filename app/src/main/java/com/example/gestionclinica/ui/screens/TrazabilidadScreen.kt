package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.viewmodel.TrazabilidadViewModel

@Composable
fun TrazabilidadScreen(
    trazabilidadViewModel: TrazabilidadViewModel,
    onVolver: () -> Unit
) {
    val registros = trazabilidadViewModel.historial.reversed() // Mostrar los más recientes primero

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onVolver) {
                Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
            }
            Text(text = "Registro de Trazabilidad", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (registros.isEmpty()) {
            Text("No hay acciones registradas en el sistema aún.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                items(registros, key = { it.id }) { registro ->
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = registro.accionRealizada, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                Text(text = registro.fechaHora, style = MaterialTheme.typography.labelSmall)
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "Documento ID: ${registro.documentoId} | Usuario ID: ${registro.usuarioId}", style = MaterialTheme.typography.bodySmall)
                            if (registro.detalles != null) {
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(text = "Detalle: ${registro.detalles}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }
        }
    }
}