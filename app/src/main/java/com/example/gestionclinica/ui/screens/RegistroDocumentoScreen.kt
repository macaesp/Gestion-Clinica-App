package com.example.gestionclinica.ui.screens
import android.os.Build
import android.widget.Toast
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.ui.components.DropdownField
import com.example.gestionclinica.ui.components.FormField
import com.example.gestionclinica.ui.components.InfoRow
import com.example.gestionclinica.ui.components.MiniaturasPaginas
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.SectionCard
import com.example.gestionclinica.ui.components.rememberEscanerDocumento
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.viewmodel.DocumentoViewModel

private val tiposDocumento = listOf("Laboral", "Permiso", "Capacitacion", "General")
private val estadosDocumento = listOf("Vigente", "Pendiente", "Vencido")

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RegistroDocumentoScreen(
    viewModel: DocumentoViewModel,
    usuarioId: Int,
    esFuncionario: Boolean = false,
    edicionDirectaRecursosHumanos: Boolean = false,
    funcionarioId: Int? = null,
    mensajeSolicitud: String? = null,
    enviandoSolicitud: Boolean = false,
    onDocumentoGuardado: () -> Unit,
    onSolicitudCambio: (Documento, List<String>) -> Unit = { _, _ -> },
    onEdicionDirectaGuardada: (Documento, List<String>, (Boolean) -> Unit) -> Unit = { _, _, completar -> completar(false) },
    onVolver: (() -> Unit)? = null
) {
    val enEdicion = viewModel.documentoEnEdicion != null
    val context = LocalContext.current
    LaunchedEffect(esFuncionario, enEdicion, funcionarioId) {
        if (esFuncionario && !enEdicion && funcionarioId != null &&
            viewModel.funcionarioId != funcionarioId
        ) {
            viewModel.cambiarFuncionario(funcionarioId)
        }
        if (esFuncionario && !enEdicion && viewModel.estado.isBlank()) {
            viewModel.cambiarEstado("Pendiente")
        }
    }
    var errorEscaner by remember { mutableStateOf<String?>(null) }
    var guardandoDirecto by remember { mutableStateOf(false) }
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
            title = when {
                !enEdicion -> "Registrar documento"
                edicionDirectaRecursosHumanos -> "Actualizar documento"
                else -> "Solicitar modificación"
            },
            subtitle = when {
                !enEdicion -> "Completa los datos del documento"
                edicionDirectaRecursosHumanos -> "Corrige los datos y confirma que está vigente"
                else -> "La versión vigente se mantiene hasta la aprobación"
            },
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
                if (enEdicion) {
                    InfoRow("Funcionario", "ID #${viewModel.funcionarioId ?: ""}")
                    InfoRow("Categoría", viewModel.tipo)
                } else if (esFuncionario) {
                    InfoRow("Mi expediente", "ID #${funcionarioId ?: viewModel.funcionarioId ?: "—"}")
                } else FormField(
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
                if (!enEdicion) DropdownField(
                    value = viewModel.tipo,
                    label = "Categoría de documento",
                    options = tiposDocumento,
                    onSelect = viewModel::cambiarTipo,
                    error = viewModel.errorTipo
                )
                Spacer(modifier = Modifier.height(4.dp))
                if (!esFuncionario) {
                    DropdownField(
                        value = viewModel.estado,
                        label = "Estado general del documento",
                        options = estadosDocumento,
                        onSelect = viewModel::cambiarEstado,
                        error = viewModel.errorEstado
                    )
                }
            }

            if (enEdicion && edicionDirectaRecursosHumanos) {
                Text(
                    "Al guardar, se cerrará cualquier solicitud pendiente y quedará registrada la versión anterior.",
                    color = MaterialTheme.colorScheme.primary
                )
            } else if (enEdicion) {
                SectionCard(title = "Motivo de la solicitud") {
                    FormField(
                        value = viewModel.motivoCambio,
                        onValueChange = viewModel::cambiarMotivoCambio,
                        label = "¿Qué necesitas corregir o actualizar?",
                        error = viewModel.errorMotivoCambio
                    )
                }
                if (!edicionDirectaRecursosHumanos) {
                    mensajeSolicitud?.let {
                        Text(it, color = MaterialTheme.colorScheme.primary)
                    }
                }
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
                onClick = {
                    if (enEdicion && edicionDirectaRecursosHumanos) {
                        val edicion = viewModel.prepararEdicionDirectaRRHH()
                        if (edicion != null) {
                            guardandoDirecto = true
                            onEdicionDirectaGuardada(edicion.first, edicion.second) { guardado ->
                                guardandoDirecto = false
                                Toast.makeText(
                                    context,
                                    if (guardado) "Cambios guardados y documento marcado como vigente."
                                    else "No se pudieron guardar los cambios.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    } else if (enEdicion) {
                        val solicitud = viewModel.prepararSolicitudCambio()
                        if (solicitud != null) {
                            onSolicitudCambio(solicitud.first, solicitud.second)
                        }
                    } else if (viewModel.guardarDocumento(usuarioId)) {
                        onDocumentoGuardado()
                    }
                },
                shape = MaterialTheme.shapes.medium,
                enabled = !enviandoSolicitud && !guardandoDirecto,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
            ) {
                Text(
                    when {
                        (enviandoSolicitud || guardandoDirecto) && edicionDirectaRecursosHumanos -> "Guardando…"
                        enviandoSolicitud -> "Enviando…"
                        !enEdicion -> "Guardar documento"
                        edicionDirectaRecursosHumanos -> "Guardar cambios y marcar vigente"
                        else -> "Enviar solicitud a revisión"
                    }
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }
    }
}
