package com.example.gestionclinica.ui.screens
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.EspecificacionDocumento
import com.example.gestionclinica.model.RegistroTrazabilidad
import com.example.gestionclinica.ui.components.EmptyState
import com.example.gestionclinica.ui.components.IconBadge
import com.example.gestionclinica.ui.components.InfoRow
import com.example.gestionclinica.ui.components.MiniaturasPaginas
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.SectionCard
import com.example.gestionclinica.ui.components.StatusChip
import com.example.gestionclinica.ui.components.icono
import com.example.gestionclinica.ui.components.nombreCategoria
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun DetalleDocumentoScreen(
    viewModel: DocumentoViewModel,
    onVolver: () -> Unit,
    historial: List<RegistroTrazabilidad> = emptyList(),
    nombreUsuario: (Int) -> String = { "Usuario #$it" },
    mostrarHistorial: Boolean = true,
    puedeModificar: Boolean = false,
    onEditar: () -> Unit = {}
) {
    val documento = viewModel.documentoSeleccionado

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = if (documento != null) "Documento #${documento.id}" else "Documento",
            subtitle = "Detalle",
            onBack = onVolver
        )

        if (documento == null) {
            EmptyState(
                icon = Icons.Default.Description,
                title = "Sin documento",
                message = "No hay un documento seleccionado."
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
            // Resumen principal
            SectionCard {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconBadge(icon = documento.clasificacion.icono(), size = 52.dp)
                    Spacer(modifier = Modifier.width(14.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = documento.clasificacion.nombreCategoria(),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Cargado el ${documento.fechaCarga}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    StatusChip(text = documento.estado)
                }
            }

            if (puedeModificar) {
                OutlinedButton(
                    onClick = onEditar,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Modificar documento")
                }
            }

            if (documento.paginasEscaneadas.isNotEmpty()) {
                SectionCard(title = "Documento escaneado") {
                    MiniaturasPaginas(
                        paginas = documento.paginasEscaneadas,
                        ancho = 120.dp,
                        alto = 160.dp
                    )
                    Text(
                        text = "${documento.paginasEscaneadas.size} página(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }

            SectionCard(title = "Información general") {
                InfoRow("ID documento", documento.id.toString())
                InfoRow("ID funcionario", documento.funcionarioId.toString())
                InfoRow("Estado general", documento.estado)
                InfoRow("Fecha de carga", documento.fechaCarga)
            }

            SectionCard(title = "Información específica") {
                when (val esp = documento.clasificacion) {
                    is EspecificacionDocumento.Laboral -> {
                        InfoRow("Subtipo", esp.subtipo)
                    }
                    is EspecificacionDocumento.Permiso -> {
                        InfoRow("Tipo de solicitud", esp.tipoSolicitud)
                        InfoRow("Periodo", "${esp.fechaInicio} a ${esp.fechaFin}")
                        InfoRow("Aprobación", esp.estadoAprobacion)
                        InfoRow("Respaldo", esp.respaldo)
                    }
                    is EspecificacionDocumento.Capacitacion -> {
                        InfoRow("Curso", esp.nombreCurso)
                        InfoRow("Duración", "${esp.cantidadHoras} horas")
                        InfoRow("Validación", esp.estadoValidacion)
                        InfoRow("Certificado", esp.certificadoRespaldo)
                    }
                    is EspecificacionDocumento.General -> {
                        Text(
                            text = "No hay detalles específicos adicionales.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            if (mostrarHistorial) {
                SectionCard(title = "Historial del documento") {
                    if (historial.isEmpty()) {
                        Text("Sin movimientos registrados.", style = MaterialTheme.typography.bodyMedium)
                    } else {
                        historial.take(20).forEach { registro ->
                            Column(modifier = Modifier.padding(vertical = 6.dp)) {
                                Text(
                                    "${registro.accionRealizada} · ${nombreUsuario(registro.usuarioId)}",
                                    style = MaterialTheme.typography.bodyLarge
                                )
                                registro.detalles?.let {
                                    Text(it, style = MaterialTheme.typography.bodySmall)
                                }
                                Text(registro.fechaHora, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        }
    }
}
