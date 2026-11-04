package com.example.gestionclinica.ui.screens
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DocumentScanner
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.ui.components.DropdownField
import com.example.gestionclinica.ui.components.FormField
import com.example.gestionclinica.ui.components.MiniaturasPaginas
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.SectionCard
import com.example.gestionclinica.ui.components.rememberEscanerDocumento
import com.example.gestionclinica.viewmodel.DocumentoViewModel

private val tiposDocumento = listOf("Laboral", "Permiso", "Capacitacion", "General")
private val estadosDocumento = listOf("Vigente", "Pendiente", "Vencido")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroDocumentoScreen(
    viewModel: DocumentoViewModel,
    onDocumentoGuardado: () -> Unit,
    onVolver: (() -> Unit)? = null
) {
    var errorEscaner by remember { mutableStateOf<String?>(null) }
    val escanear = rememberEscanerDocumento(
        onResultado = { paginas, pdf ->
            errorEscaner = null
            viewModel.establecerEscaneo(paginas, pdf)
        },
        onError = { errorEscaner = it }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .imePadding()
    ) {
        ScreenHeader(
            title = "Registrar documento",
            subtitle = "Completa los datos del documento",
            onBack = onVolver
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            SectionCard(title = "Datos generales") {
                Spacer(modifier = Modifier.height(4.dp))
                FormField(
                    value = viewModel.funcionarioId?.toString() ?: "",
                    onValueChange = { nuevo ->
                        val id = if (nuevo.isBlank()) null else nuevo.toIntOrNull()
                        viewModel.cambiarFuncionario(id)
                    },
                    label = "ID del funcionario",
                    keyboardType = KeyboardType.Number,
                    error = viewModel.errorFuncionario
                )
                Spacer(modifier = Modifier.height(4.dp))
                DropdownField(
                    value = viewModel.tipo,
                    label = "Categoría de documento",
                    options = tiposDocumento,
                    onSelect = viewModel::cambiarTipo,
                    error = viewModel.errorTipo
                )
                Spacer(modifier = Modifier.height(4.dp))
                DropdownField(
                    value = viewModel.estado,
                    label = "Estado general del documento",
                    options = estadosDocumento,
                    onSelect = viewModel::cambiarEstado,
                    error = viewModel.errorEstado
                )
            }

            SectionCard(title = "Documento escaneado") {
                if (viewModel.paginasEscaneadas.isEmpty()) {
                    Text(
                        text = "Digitaliza el documento con la cámara. Los bordes se recortan y la imagen se mejora automáticamente.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = escanear,
                        shape = MaterialTheme.shapes.medium,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                    ) {
                        Icon(Icons.Default.DocumentScanner, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Escanear documento")
                    }
                } else {
                    MiniaturasPaginas(paginas = viewModel.paginasEscaneadas)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "${viewModel.paginasEscaneadas.size} página(s) escaneada(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            onClick = escanear,
                            shape = MaterialTheme.shapes.medium,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Volver a escanear")
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        TextButton(onClick = viewModel::quitarEscaneo) {
                            Text("Quitar")
                        }
                    }
                }
                val error = errorEscaner
                if (error != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            when (viewModel.tipo) {
                "Laboral" -> SectionCard(title = "Detalles laborales") {
                    FormField(
                        value = viewModel.subtipoLaboral,
                        onValueChange = viewModel::cambiarSubtipoLaboral,
                        label = "Subtipo",
                        placeholder = "Ej: Contrato, Anexo"
                    )
                }

                "Permiso" -> SectionCard(title = "Detalles del permiso") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FormField(viewModel.tipoSolicitud, viewModel::cambiarTipoSolicitud, "Tipo de solicitud")
                        FormField(viewModel.fechaInicio, viewModel::cambiarFechaInicio, "Fecha de inicio", placeholder = "YYYY-MM-DD")
                        FormField(viewModel.fechaFin, viewModel::cambiarFechaFin, "Fecha de término", placeholder = "YYYY-MM-DD")
                        FormField(viewModel.estadoAprobacion, viewModel::cambiarEstadoAprobacion, "Estado de aprobación")
                        FormField(viewModel.respaldo, viewModel::cambiarRespaldo, "Respaldo adjunto")
                    }
                }

                "Capacitacion" -> SectionCard(title = "Detalles de la capacitación") {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        FormField(viewModel.nombreCurso, viewModel::cambiarNombreCurso, "Nombre del curso")
                        FormField(
                            viewModel.cantidadHoras,
                            viewModel::cambiarCantidadHoras,
                            "Cantidad de horas",
                            keyboardType = KeyboardType.Number
                        )
                        FormField(viewModel.certificadoRespaldo, viewModel::cambiarCertificadoRespaldo, "Certificado de respaldo")
                        FormField(viewModel.estadoValidacion, viewModel::cambiarEstadoValidacion, "Estado de validación")
                    }
                }
            }

            Button(
                onClick = { if (viewModel.guardarDocumento()) onDocumentoGuardado() },
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text("Guardar documento")
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}