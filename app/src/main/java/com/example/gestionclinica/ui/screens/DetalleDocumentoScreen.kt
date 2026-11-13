package com.example.gestionclinica.ui.screens
import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Paint
import android.graphics.RectF
import android.graphics.pdf.PdfDocument
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import com.example.gestionclinica.model.EspecificacionDocumento
import com.example.gestionclinica.model.RegistroTrazabilidad
import com.example.gestionclinica.data.toDocumento
import com.example.gestionclinica.ui.components.EmptyState
import com.example.gestionclinica.ui.components.IconBadge
import com.example.gestionclinica.ui.components.InfoRow
import com.example.gestionclinica.ui.components.MiniaturasPaginas
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.SectionCard
import com.example.gestionclinica.ui.components.StatusChip
import com.example.gestionclinica.ui.components.icono
import com.example.gestionclinica.ui.components.nombreCategoria
import com.example.gestionclinica.ui.components.resumen
import com.example.gestionclinica.viewmodel.DocumentoViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun DetalleDocumentoScreen(
    viewModel: DocumentoViewModel,
    onVolver: () -> Unit,
    historial: List<RegistroTrazabilidad> = emptyList(),
    nombreUsuario: (Int) -> String = { "Usuario #$it" },
    mostrarHistorial: Boolean = true,
    puedeSolicitarCambio: Boolean = false,
    etiquetaAccionEdicion: String = "Solicitar modificación",
    onEditar: () -> Unit = {}
) {
    val documento = viewModel.documentoSeleccionado
    LaunchedEffect(documento?.id) {
        documento?.id?.let(viewModel::cargarVersiones)
    }
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var exportando by remember { mutableStateOf(false) }
    val exportarPdfLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.CreateDocument("application/pdf")
    ) { destino ->
        val documentoActual = documento
        if (destino != null && documentoActual != null) {
            coroutineScope.launch {
                exportando = true
                try {
                    withContext(Dispatchers.IO) {
                        exportarDocumentoPdf(context, documentoActual, destino)
                    }
                    Toast.makeText(context, "PDF guardado correctamente.", Toast.LENGTH_SHORT).show()
                } catch (_: Exception) {
                    Toast.makeText(context, "No se pudo guardar el PDF.", Toast.LENGTH_LONG).show()
                } finally {
                    exportando = false
                }
            }
        }
    }

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

            if (puedeSolicitarCambio) {
                OutlinedButton(
                    onClick = onEditar,
                    modifier = Modifier.fillMaxWidth().height(52.dp)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(etiquetaAccionEdicion)
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
                    OutlinedButton(
                        onClick = {
                            exportarPdfLauncher.launch("documento_${documento.id}.pdf")
                        },
                        enabled = !exportando,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 12.dp)
                    ) {
                        Icon(Icons.Default.Download, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (exportando) "Guardando PDF…" else "Descargar PDF")
                    }
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

            val versiones = viewModel.versionesDocumento.filter { it.documentoId == documento.id }
            if (versiones.isNotEmpty()) {
                SectionCard(title = "Versiones anteriores") {
                    versiones.forEach { version ->
                        val anterior = version.contenido.toDocumento(documento.id)
                        InfoRow(
                            "Versión ${version.numeroVersion} · ${version.fechaRegistro}",
                            "${anterior.clasificacion.nombreCategoria()} · ${anterior.clasificacion.resumen().orEmpty()} · ${anterior.estado}"
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

private fun exportarDocumentoPdf(context: Context, documento: com.example.gestionclinica.model.Documento, destino: Uri) {
    val pdfOrigen = documento.pdfEscaneado
    if (!pdfOrigen.isNullOrBlank()) {
        val entrada = context.contentResolver.openInputStream(Uri.parse(pdfOrigen))
            ?: error("No se pudo leer el PDF original")
        val salida = context.contentResolver.openOutputStream(destino, "w")
            ?: error("No se pudo crear el archivo de destino")
        entrada.use { input -> salida.use { output -> input.copyTo(output) } }
        return
    }

    check(documento.paginasEscaneadas.isNotEmpty()) { "El documento no tiene páginas escaneadas" }
    val salida = context.contentResolver.openOutputStream(destino, "w")
        ?: error("No se pudo crear el archivo de destino")
    val pdf = PdfDocument()
    try {
        documento.paginasEscaneadas.forEachIndexed { index, ruta ->
            val entrada = context.contentResolver.openInputStream(Uri.parse(ruta))
                ?: error("No se pudo leer una página escaneada")
            val bitmap = entrada.use { BitmapFactory.decodeStream(it) }
                ?: error("Una página escaneada no es válida")
            try {
                val anchoPagina = 595
                val altoPagina = 842
                val pagina = pdf.startPage(
                    PdfDocument.PageInfo.Builder(anchoPagina, altoPagina, index + 1).create()
                )
                pagina.canvas.drawColor(android.graphics.Color.WHITE)
                val escala = minOf(
                    anchoPagina.toFloat() / bitmap.width,
                    altoPagina.toFloat() / bitmap.height
                )
                val ancho = bitmap.width * escala
                val alto = bitmap.height * escala
                val izquierda = (anchoPagina - ancho) / 2f
                val arriba = (altoPagina - alto) / 2f
                pagina.canvas.drawBitmap(
                    bitmap,
                    null,
                    RectF(izquierda, arriba, izquierda + ancho, arriba + alto),
                    Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                )
                pdf.finishPage(pagina)
            } finally {
                bitmap.recycle()
            }
        }
        salida.use { pdf.writeTo(it) }
    } finally {
        pdf.close()
    }
}
