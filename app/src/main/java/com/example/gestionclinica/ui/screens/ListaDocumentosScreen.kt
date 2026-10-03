package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.Documento
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun ListaDocumentosScreen(
    viewModel: DocumentoViewModel,
    onDocumentoClick: (Documento) -> Unit,
    onVolver: () -> Unit,
    funcionarioFiltro: String? = null
) {

    // Si recibimos un funcionario, mostramos solamente sus documentos.
    // Si es null, mostramos todos.
    val documentosMostrados = if (funcionarioFiltro != null) {
        viewModel.obtenerDocumentosPorFuncionario(funcionarioFiltro)
    } else {
        viewModel.documentos
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        // TÍTULO
        Text(
            text = if (funcionarioFiltro != null) {
                "Documentos del funcionario"
            } else {
                "Documentos registrados"
            },
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // Si venimos desde un expediente,
        // mostramos el nombre del funcionario.
        if (funcionarioFiltro != null) {

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = funcionarioFiltro,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        // SIN DOCUMENTOS
        if (documentosMostrados.isEmpty()) {

            Text(
                text = if (funcionarioFiltro != null) {
                    "Este funcionario no tiene documentos registrados."
                } else {
                    "No hay documentos registrados."
                },
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(
                modifier = Modifier.weight(1f)
            )

        } else {

            // LISTA DE DOCUMENTOS
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {

                items(
                    items = documentosMostrados,
                    key = { documento ->
                        documento.id
                    }
                ) { documento ->

                    DocumentoCard(
                        documento = documento,
                        onClick = {
                            onDocumentoClick(documento)
                        }
                    )
                }
            }
        }

        Spacer(
            modifier = Modifier.height(16.dp)
        )

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}

@Composable
fun DocumentoCard(
    documento: Documento,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            }
    ) {

        Column(
            modifier = Modifier.padding(16.dp)
        ) {

            Text(
                text = documento.nombre,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Text(
                    text = documento.tipo,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = documento.estado,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(
                modifier = Modifier.height(8.dp)
            )

            Text(
                text = "Funcionario: ${documento.funcionario}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}