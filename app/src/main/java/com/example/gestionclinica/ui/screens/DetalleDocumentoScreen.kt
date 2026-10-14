package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.EspecificacionDocumento
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun DetalleDocumentoScreen(
    viewModel: DocumentoViewModel,
    onVolver: () -> Unit
) {
    val documento = viewModel.documentoSeleccionado

    Column(modifier = Modifier.fillMaxSize().padding(24.dp)) {
        Text(text = "Detalle del Documento", style = MaterialTheme.typography.headlineSmall)
        Spacer(modifier = Modifier.height(24.dp))

        if (documento != null) {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(text = "Información General", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(text = "ID Documento: ${documento.id}")
                    Text(text = "ID Funcionario: ${documento.funcionarioId}")
                    Text(text = "Estado General: ${documento.estado}")
                    Text(text = "Fecha de carga: ${documento.fechaCarga}")

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider()
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(text = "Información Específica", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))

                    // Mostrar datos dependiendo de la subclase
                    when (val esp = documento.clasificacion) {
                        is EspecificacionDocumento.Laboral -> {
                            Text(text = "Categoría: Laboral")
                            Text(text = "Subtipo: ${esp.subtipo}")
                        }
                        is EspecificacionDocumento.Permiso -> {
                            Text(text = "Categoría: Permiso")
                            Text(text = "Tipo Solicitud: ${esp.tipoSolicitud}")
                            Text(text = "Periodo: ${esp.fechaInicio} a ${esp.fechaFin}")
                            Text(text = "Aprobación: ${esp.estadoAprobacion}")
                            Text(text = "Respaldo: ${esp.respaldo}")
                        }
                        is EspecificacionDocumento.Capacitacion -> {
                            Text(text = "Categoría: Capacitación")
                            Text(text = "Curso: ${esp.nombreCurso}")
                            Text(text = "Duración: ${esp.cantidadHoras} horas")
                            Text(text = "Validación: ${esp.estadoValidacion}")
                            Text(text = "Certificado: ${esp.certificadoRespaldo}")
                        }
                        is EspecificacionDocumento.General -> {
                            Text(text = "Categoría: General")
                            Text(text = "No hay detalles específicos adicionales.")
                        }
                    }
                }
            }
        } else {
            Text(text = "No hay un documento seleccionado.", style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}