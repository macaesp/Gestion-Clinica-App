// Reemplaza todo el contenido de HomeScreen.kt con esto:
package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.viewmodel.AuthViewModel

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onVerFuncionarios: () -> Unit,
    onRegistrarDocumento: () -> Unit,
    onVerDocumentos: () -> Unit,
    onVerTrazabilidad: () -> Unit, // NUEVO PARÁMETRO
    onLogout: () -> Unit
) {
    val usuario = authViewModel.usuarioActual

    Column(modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp, vertical = 32.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(text = "CMQ Área Salud", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            IconButton(onClick = { authViewModel.logout(); onLogout() }) {
                Icon(imageVector = Icons.AutoMirrored.Filled.Logout, contentDescription = "Cerrar sesión")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "Bienvenido/a,", style = MaterialTheme.typography.headlineSmall)
        Text(text = usuario?.nombre ?: "", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Perfil: ${usuario?.rol?.name}", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.onSurfaceVariant)

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.People, contentDescription = null)
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "Funcionarios", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = onVerFuncionarios, modifier = Modifier.fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.People, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Consultar funcionarios")
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = null)
                    Spacer(modifier = Modifier.padding(6.dp))
                    Text(text = "Documentos", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(16.dp))

                if (usuario?.rol == RolUsuario.RECURSOS_HUMANOS || usuario?.rol == RolUsuario.ADMINISTRADOR_SISTEMA || usuario?.rol == RolUsuario.JEFATURA) {
                    Button(onClick = onRegistrarDocumento, modifier = Modifier.fillMaxWidth()) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = null)
                        Spacer(modifier = Modifier.padding(4.dp))
                        Text("Registrar documento")
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                }

                OutlinedButton(onClick = onVerDocumentos, modifier = Modifier.fillMaxWidth()) {
                    Icon(imageVector = Icons.Default.Folder, contentDescription = null)
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text("Ver documentos")
                }
            }
        }

        // NUEVA TARJETA: Solo visible para Admin y TI
        if (usuario?.rol == RolUsuario.ADMINISTRADOR_SISTEMA || usuario?.rol == RolUsuario.TI_CIBERSEGURIDAD) {
            Spacer(modifier = Modifier.height(16.dp))
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(imageVector = Icons.Default.Security, contentDescription = null)
                        Spacer(modifier = Modifier.padding(6.dp))
                        Text(text = "Auditoría", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    OutlinedButton(onClick = onVerTrazabilidad, modifier = Modifier.fillMaxWidth()) {
                        Text("Ver Trazabilidad")
                    }
                }
            }
        }
    }
}