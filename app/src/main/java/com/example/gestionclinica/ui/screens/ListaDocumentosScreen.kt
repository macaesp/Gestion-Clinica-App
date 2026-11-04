package com.example.gestionclinica.ui.screens
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.ui.components.AppCard
import com.example.gestionclinica.ui.components.EmptyState
import com.example.gestionclinica.ui.components.IconBadge
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.StatusChip
import com.example.gestionclinica.ui.components.icono
import com.example.gestionclinica.ui.components.nombreCategoria
import com.example.gestionclinica.ui.components.resumen
import com.example.gestionclinica.viewmodel.DocumentoViewModel

private val categorias = listOf("Todos", "Laboral", "Permiso", "Capacitación", "General")

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaDocumentosScreen(
    viewModel: DocumentoViewModel,
    onDocumentoClick: (Documento) -> Unit,
    onVolver: (() -> Unit)? = null,
    funcionarioFiltroId: Int? = null,
    onRegistrar: (() -> Unit)? = null
) {
    var filtro by remember { mutableStateOf("Todos") }

    val base = if (funcionarioFiltroId != null) {
        viewModel.obtenerDocumentosPorFuncionario(funcionarioFiltroId)
    } else {
        viewModel.documentos.toList()
    }
    val documentosMostrados = if (filtro == "Todos") {
        base
    } else {
        base.filter { it.clasificacion.nombreCategoria() == filtro }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = if (funcionarioFiltroId != null) "Documentos del funcionario" else "Documentos",
                subtitle = "${base.size} registrado(s)",
                onBack = onVolver
            )

            // Filtros por categoría
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                categorias.forEach { categoria ->
                    FilterChip(
                        selected = filtro == categoria,
                        onClick = { filtro = categoria },
                        label = { Text(categoria) }
                    )
                }
            }

            if (documentosMostrados.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.Folder,
                    title = "Sin documentos",
                    message = if (base.isEmpty()) {
                        "Aún no hay documentos registrados."
                    } else {
                        "No hay documentos en la categoría \"$filtro\"."
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 12.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = documentosMostrados, key = { it.id }) { documento ->
                        DocumentoCard(
                            documento = documento,
                            onClick = { onDocumentoClick(documento) }
                        )
                    }
                }
            }
        }

        if (onRegistrar != null) {
            ExtendedFloatingActionButton(
                onClick = onRegistrar,
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Registrar") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            )
        }
    }
}

@Composable
fun DocumentoCard(documento: Documento, onClick: () -> Unit) {
    val categoria = documento.clasificacion.nombreCategoria()
    val resumen = documento.clasificacion.resumen()

    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconBadge(icon = documento.clasificacion.icono())
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "$categoria · #${documento.id}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (resumen != null) {
                    Text(
                        text = resumen,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Text(
                    text = "Funcionario #${documento.funcionarioId} · ${documento.fechaCarga}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            StatusChip(text = documento.estado)
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}