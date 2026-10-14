package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.model.EspecificacionDocumento
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun ListaDocumentosScreen(
    viewModel: DocumentoViewModel,
    onDocumentoClick: (Documento) -> Unit,
    onVolver: () -> Unit,
    funcionarioFiltroId: Int? = null
) {
    val documentosMostrados = if (funcionarioFiltroId != null) {
        viewModel.obtenerDocumentosPorFuncionario(funcionarioFiltroId)
    } else {
        viewModel.documentos
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp)
    ) {
        Text(
            text = if (funcionarioFiltroId != null) "Documentos del funcionario" else "Documentos registrados",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (documentosMostrados.isEmpty()) {
            Text(
                text = "No hay documentos registrados.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.weight(1f))
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(items = documentosMostrados, key = { it.id }) { documento ->
                    DocumentoCard(documento = documento, onClick = { onDocumentoClick(documento) })
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onVolver, modifier = Modifier.fillMaxWidth()) {
            Text("Volver")
        }
    }
}

@Composable
fun DocumentoCard(documento: Documento, onClick: () -> Unit) {
    // Determinamos qué mostrar según la clasificación
    val tipoTexto = when (documento.clasificacion) {
        is EspecificacionDocumento.Laboral -> "Laboral"
        is EspecificacionDocumento.Permiso -> "Permiso"
        is EspecificacionDocumento.Capacitacion -> "Capacitación"
        is EspecificacionDocumento.General -> "General"
    }

    Card(modifier = Modifier.fillMaxWidth().clickable { onClick() }) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Documento #$tipoTexto", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(8.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(text = "ID Funcionario: ${documento.funcionarioId}", style = MaterialTheme.typography.bodyMedium)
                Text(text = documento.estado, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.primary)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Fecha carga: ${documento.fechaCarga}", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}