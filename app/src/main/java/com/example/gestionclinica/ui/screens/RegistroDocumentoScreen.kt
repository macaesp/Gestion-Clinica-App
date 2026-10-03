package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroDocumentoScreen(
    viewModel: DocumentoViewModel,
    onDocumentoGuardado: () -> Unit
) {

    // Controla si los menús están abiertos o cerrados
    var menuTipoAbierto by remember { mutableStateOf(false) }
    var menuEstadoAbierto by remember { mutableStateOf(false) }

    // Opciones para el tipo de documento
    val tiposDocumento = listOf(
        "Contrato",
        "Anexo",
        "Certificado",
        "Permiso",
        "Capacitación",
        "Otro"
    )

    // Opciones disponibles para el estado
    val estadosDocumento = listOf(
        "Vigente",
        "Pendiente",
        "Vencido"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Registrar Documento",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Nombre del Documento
        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = viewModel::cambiarNombre,
            label = {
                Text("Nombre del Documento")
            },
            isError = viewModel.errorNombre != null,
            supportingText = {
                viewModel.errorNombre?.let { mensaje ->
                    Text(mensaje)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Tipo Documento
        ExposedDropdownMenuBox(
            expanded = menuTipoAbierto,
            onExpandedChange = {
                menuTipoAbierto = !menuTipoAbierto
            }
        ) {

            OutlinedTextField(
                value = viewModel.tipo,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Tipo de Documento")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuTipoAbierto
                    )
                },
                isError = viewModel.errorTipo != null,
                supportingText = {
                    viewModel.errorTipo?.let { mensaje ->
                        Text(mensaje)
                    }
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuTipoAbierto,
                onDismissRequest = {
                    menuTipoAbierto = false
                }
            ) {

                tiposDocumento.forEach { tipo ->

                    DropdownMenuItem(
                        text = {
                            Text(tipo)
                        },
                        onClick = {
                            viewModel.cambiarTipo(tipo)
                            menuTipoAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Ver Funcionario
        OutlinedTextField(
            value = viewModel.funcionario,
            onValueChange = viewModel::cambiarFuncionario,
            label = {
                Text("Funcionario")
            },
            isError = viewModel.errorFuncionario != null,
            supportingText = {
                viewModel.errorFuncionario?.let { mensaje ->
                    Text(mensaje)
                }
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Ver el estado del documento
        ExposedDropdownMenuBox(
            expanded = menuEstadoAbierto,
            onExpandedChange = {
                menuEstadoAbierto = !menuEstadoAbierto
            }
        ) {

            OutlinedTextField(
                value = viewModel.estado,
                onValueChange = {},
                readOnly = true,
                label = {
                    Text("Estado")
                },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(
                        expanded = menuEstadoAbierto
                    )
                },
                isError = viewModel.errorEstado != null,
                supportingText = {
                    viewModel.errorEstado?.let { mensaje ->
                        Text(mensaje)
                    }
                },
                modifier = Modifier
                    .menuAnchor(
                        type = ExposedDropdownMenuAnchorType.PrimaryNotEditable,
                        enabled = true
                    )
                    .fillMaxWidth()
            )

            ExposedDropdownMenu(
                expanded = menuEstadoAbierto,
                onDismissRequest = {
                    menuEstadoAbierto = false
                }
            ) {

                estadosDocumento.forEach { estado ->

                    DropdownMenuItem(
                        text = {
                            Text(estado)
                        },
                        onClick = {
                            viewModel.cambiarEstado(estado)
                            menuEstadoAbierto = false
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Boton para Guardar
        Button(
            onClick = {

                val formularioValido =
                    viewModel.guardarDocumento()

                if (formularioValido) {
                    onDocumentoGuardado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {

            Text("Guardar Documento")
        }
    }
}