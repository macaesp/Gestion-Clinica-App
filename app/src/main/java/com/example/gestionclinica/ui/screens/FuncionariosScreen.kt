package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.model.Funcionario
import com.example.gestionclinica.ui.components.AppCard
import com.example.gestionclinica.ui.components.EmptyState
import com.example.gestionclinica.ui.components.InitialsAvatar
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.ui.components.StatusChip
import com.example.gestionclinica.viewmodel.FuncionarioViewModel

@Composable
fun FuncionariosScreen(
    viewModel: FuncionarioViewModel,
    onFuncionarioClick: (Funcionario) -> Unit,
    onVolver: (() -> Unit)? = null,
    onAgregar: (() -> Unit)? = null
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize()) {
            ScreenHeader(
                title = "Funcionarios",
                subtitle = "CMQ – Área Salud",
                onBack = onVolver
            )

            OutlinedTextField(
                value = viewModel.busqueda,
                onValueChange = { viewModel.cambiarBusqueda(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                placeholder = { Text("Buscar por nombre, RUT o cargo") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                trailingIcon = {
                    if (viewModel.busqueda.isNotEmpty()) {
                        IconButton(onClick = { viewModel.limpiarBusqueda() }) {
                            Icon(Icons.Default.Close, contentDescription = "Limpiar búsqueda")
                        }
                    }
                },
                singleLine = true,
                shape = MaterialTheme.shapes.extraLarge
            )

            Spacer(modifier = Modifier.height(12.dp))

            val lista = viewModel.funcionariosFiltrados

            if (lista.isEmpty()) {
                EmptyState(
                    icon = Icons.Default.People,
                    title = "Sin resultados",
                    message = "No se encontraron funcionarios con esa búsqueda."
                )
            } else {
                Text(
                    text = "${lista.size} funcionario(s)",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 8.dp, bottom = 96.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(items = lista, key = { it.id }) { funcionario ->
                        FuncionarioCard(
                            funcionario = funcionario,
                            onClick = { onFuncionarioClick(funcionario) }
                        )
                    }
                }
            }
        }

        if (onAgregar != null) {
            ExtendedFloatingActionButton(
                onClick = onAgregar,
                icon = { Icon(Icons.Default.PersonAdd, contentDescription = null) },
                text = { Text("Agregar") },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(20.dp)
            )
        }
    }
}

@Composable
private fun FuncionarioCard(funcionario: Funcionario, onClick: () -> Unit) {
    AppCard(onClick = onClick) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            InitialsAvatar(name = funcionario.nombre)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = funcionario.nombre,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "${funcionario.cargo} · ${funcionario.centroSalud}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = funcionario.rut,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                StatusChip(text = funcionario.estado)
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}