package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun DetalleDocumentoScreen(
    viewModel: DocumentoViewModel,
    onVolver: () -> Unit
) {

    val documento = viewModel.documentoSeleccionado

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {

        Text(
            text = "Detalle del Documento",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (documento != null) {

            Card(
                modifier = Modifier.fillMaxWidth()
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text(
                        text = documento.nombre,
                        style = MaterialTheme.typography.titleLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "ID: ${documento.id}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Tipo: ${documento.tipo}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Funcionario: ${documento.funcionario}"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Estado: ${documento.estado}"
                    )
                }
            }

        } else {

            Text(
                text = "No hay un documento seleccionado.",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onVolver,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Volver")
        }
    }
}