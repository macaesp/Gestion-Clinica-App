package com.example.gestionclinica.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistroDocumentoScreen(
    viewModel: DocumentoViewModel,
    onDocumentoGuardado: () -> Unit
) {
    var menuTipoAbierto by remember { mutableStateOf(false) }
    var menuEstadoAbierto by remember { mutableStateOf(false) }

    val tiposDocumento = listOf("Laboral", "Permiso", "Capacitacion", "General")
    val estadosDocumento = listOf("Vigente", "Pendiente", "Vencido")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(text = "Registrar Documento", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        // ID Funcionario (Solo lectura)
        OutlinedTextField(
            value = viewModel.funcionarioId?.toString() ?: "",
            onValueChange = { },
            readOnly = true,
            label = { Text("ID Funcionario") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Tipo Documento
        ExposedDropdownMenuBox(
            expanded = menuTipoAbierto,
            onExpandedChange = { menuTipoAbierto = !menuTipoAbierto }
        ) {
            OutlinedTextField(
                value = viewModel.tipo,
                onValueChange = {},
                readOnly = true,
                label = { Text("Categoría de Documento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuTipoAbierto) },
                modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = menuTipoAbierto, onDismissRequest = { menuTipoAbierto = false }) {
                tiposDocumento.forEach { tipo ->
                    DropdownMenuItem(
                        text = { Text(tipo) },
                        onClick = { viewModel.cambiarTipo(tipo); menuTipoAbierto = false }
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        // CAMPOS DINÁMICOS SEGÚN EL TIPO
        if (viewModel.tipo == "Laboral") {
            Text("Detalles Laborales", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(
                value = viewModel.subtipoLaboral,
                onValueChange = viewModel::cambiarSubtipoLaboral,
                label = { Text("Subtipo (Ej: Contrato, Anexo)") },
                modifier = Modifier.fillMaxWidth()
            )
        } else if (viewModel.tipo == "Permiso") {
            Text("Detalles de Permiso", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = viewModel.tipoSolicitud, onValueChange = viewModel::cambiarTipoSolicitud, label = { Text("Tipo de Solicitud") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.fechaInicio, onValueChange = viewModel::cambiarFechaInicio, label = { Text("Fecha Inicio (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.fechaFin, onValueChange = viewModel::cambiarFechaFin, label = { Text("Fecha Fin (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.estadoAprobacion, onValueChange = viewModel::cambiarEstadoAprobacion, label = { Text("Estado Aprobación") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.respaldo, onValueChange = viewModel::cambiarRespaldo, label = { Text("Respaldo Adjunto") }, modifier = Modifier.fillMaxWidth())
        } else if (viewModel.tipo == "Capacitacion") {
            Text("Detalles de Capacitación", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(value = viewModel.nombreCurso, onValueChange = viewModel::cambiarNombreCurso, label = { Text("Nombre del Curso") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.cantidadHoras, onValueChange = viewModel::cambiarCantidadHoras, label = { Text("Cantidad de Horas") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.certificadoRespaldo, onValueChange = viewModel::cambiarCertificadoRespaldo, label = { Text("Certificado Respaldo") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = viewModel.estadoValidacion, onValueChange = viewModel::cambiarEstadoValidacion, label = { Text("Estado Validación") }, modifier = Modifier.fillMaxWidth())
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Estado General
        ExposedDropdownMenuBox(
            expanded = menuEstadoAbierto,
            onExpandedChange = { menuEstadoAbierto = !menuEstadoAbierto }
        ) {
            OutlinedTextField(
                value = viewModel.estado,
                onValueChange = {},
                readOnly = true,
                label = { Text("Estado General del Documento") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = menuEstadoAbierto) },
                modifier = Modifier.menuAnchor(type = ExposedDropdownMenuAnchorType.PrimaryNotEditable).fillMaxWidth()
            )
            ExposedDropdownMenu(expanded = menuEstadoAbierto, onDismissRequest = { menuEstadoAbierto = false }) {
                estadosDocumento.forEach { estado ->
                    DropdownMenuItem(
                        text = { Text(estado) },
                        onClick = { viewModel.cambiarEstado(estado); menuEstadoAbierto = false }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {
                if (viewModel.guardarDocumento()) onDocumentoGuardado()
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Documento")
        }
    }
}