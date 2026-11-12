package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.style.TextAlign
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.ui.components.DropdownField
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.etiqueta
import com.example.gestionclinica.viewmodel.AuthViewModel

@Composable
fun GestionUsuariosScreen(
    authViewModel: AuthViewModel,
    onVolver: () -> Unit
) {
    val usuarios by authViewModel.usuarios.collectAsState()
    val centros by authViewModel.centrosDisponibles.collectAsState()
    var nombre by remember { mutableStateOf("") }
    var rut by remember { mutableStateOf("") }
    var clave by remember { mutableStateOf("") }
    var rolSeleccionado by remember { mutableStateOf(RolUsuario.FUNCIONARIO) }
    var centroSeleccionado by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        authViewModel.cargarUsuarios()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp)
    ) {
        ScreenHeader(
            title = "Administrar cuentas",
            subtitle = "Crear y habilitar usuarios",
            onBack = onVolver
        )

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text("Nueva cuenta", style = MaterialTheme.typography.titleMedium)

                        OutlinedTextField(
                            value = nombre,
                            onValueChange = { nombre = it },
                            label = { Text("Nombre completo") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = rut,
                            onValueChange = { rut = it },
                            label = { Text("RUT") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )
                        OutlinedTextField(
                            value = clave,
                            onValueChange = { clave = it },
                            label = { Text("Contraseña inicial (mínimo 8 caracteres)") },
                            singleLine = true,
                            visualTransformation = PasswordVisualTransformation(),
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Password
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )

                        Text("Perfil", style = MaterialTheme.typography.labelLarge)
                        RolUsuario.entries.forEach { rol ->
                            OutlinedButton(
                                onClick = { rolSeleccionado = rol },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    if (rol == rolSeleccionado) {
                                        "✓ ${rol.etiqueta()}"
                                    } else {
                                        rol.etiqueta()
                                    }
                                )
                            }
                        }

                        if (rolSeleccionado == RolUsuario.JEFATURA ||
                            rolSeleccionado == RolUsuario.ADMINISTRATIVO
                        ) {
                            DropdownField(
                                value = centroSeleccionado,
                                label = "Centro asignado",
                                options = centros,
                                onSelect = { centroSeleccionado = it },
                                error = if (centros.isEmpty()) {
                                    "Registra primero un centro de salud para poder asignarlo."
                                } else null
                            )
                        }

                        mensaje?.let {
                            Text(it, color = MaterialTheme.colorScheme.error)
                        }

                        Button(
                            onClick = {
                                authViewModel.crearUsuario(
                                    nombre = nombre,
                                    rut = rut,
                                    rol = rolSeleccionado,
                                    claveInicial = clave,
                                    centroAsignado = centroSeleccionado.takeIf {
                                        rolSeleccionado == RolUsuario.JEFATURA ||
                                            rolSeleccionado == RolUsuario.ADMINISTRATIVO
                                    }
                                ) { error ->
                                    mensaje = error ?: "Cuenta creada."
                                    if (error == null) {
                                        nombre = ""
                                        rut = ""
                                        clave = ""
                                        centroSeleccionado = ""
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Crear cuenta")
                        }
                    }
                }
            }

            item {
                Text(
                    "Cuentas registradas",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            items(usuarios, key = { it.id }) { usuario ->
                var centroUsuario by remember(usuario.id, usuario.centroAsignado) {
                    mutableStateOf(usuario.centroAsignado.orEmpty())
                }
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(usuario.nombre, style = MaterialTheme.typography.titleSmall)
                        Text("${usuario.rut} · ${usuario.rol.etiqueta()}")
                        Text(if (usuario.activo) "Activa" else "Inactiva")

                        if (usuario.rol == RolUsuario.JEFATURA ||
                            usuario.rol == RolUsuario.ADMINISTRATIVO
                        ) {
                            DropdownField(
                                value = centroUsuario,
                                label = "Centro asignado",
                                options = centros,
                                onSelect = { centroUsuario = it }
                            )
                            OutlinedButton(
                                onClick = {
                                    authViewModel.asignarCentroUsuario(
                                        usuario.id,
                                        centroUsuario.takeIf { it.isNotBlank() }
                                    )
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text("Guardar centro asignado")
                            }
                        }

                        Spacer(Modifier.height(4.dp))

                        OutlinedButton(
                            onClick = {
                                authViewModel.cambiarEstadoUsuario(
                                    usuario.id,
                                    activo = !usuario.activo
                                )
                            },
                            enabled = usuario.id != authViewModel.usuarioActual?.id || usuario.activo
                        ) {
                            Text(if (usuario.activo) "Desactivar" else "Activar")
                        }
                    }
                }
            }
        }
    }
}
