package com.example.gestionclinica.ui.registro

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun RegistroDocumentoScreen(
    viewModel: DocumentoViewModel,
    onDocumentoGuardado: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {

        Text(
            text = "Registrar Documento",
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = viewModel.nombre,
            onValueChange = {
                viewModel.cambiarNombre(it)
            },
            label = {
                Text("Nombre del Documento")
            },
            isError = viewModel.errorNombre != null,
            supportingText = {
                viewModel.errorNombre?.let { mensaje ->
                    Text(mensaje)
                }
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.tipo,
            onValueChange = {
                viewModel.cambiarTipo(it)
            },
            label = {
                Text("Tipo de Documento")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.funcionario,
            onValueChange = {
                viewModel.cambiarFuncionario(it)
            },
            label = {
                Text("Funcionario")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        OutlinedTextField(
            value = viewModel.estado,
            onValueChange = {
                viewModel.cambiarEstado(it)
            },
            label = {
                Text("Estado")
            },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = {

                val guardadoCorrectamente =
                    viewModel.guardarDocumento()

                if (guardadoCorrectamente) {
                    onDocumentoGuardado()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Guardar Documento")
        }

        viewModel.mensajeExito?.let { mensaje ->

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = mensaje
            )
        }
    }
}