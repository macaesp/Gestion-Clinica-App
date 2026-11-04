package com.example.gestionclinica.ui.screens
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.ui.components.AppCard
import com.example.gestionclinica.ui.components.EmptyState
import com.example.gestionclinica.ui.components.IconBadge
import com.example.gestionclinica.ui.components.ScreenHeader
import com.example.gestionclinica.viewmodel.TrazabilidadViewModel

@Composable
fun TrazabilidadScreen(
    trazabilidadViewModel: TrazabilidadViewModel,
    onVolver: (() -> Unit)? = null
) {
    val registros = trazabilidadViewModel.historial.reversed() // más recientes primero

    Column(modifier = Modifier.fillMaxSize()) {
        ScreenHeader(
            title = "Trazabilidad",
            subtitle = "Acciones realizadas sobre los documentos",
            onBack = onVolver
        )

        if (registros.isEmpty()) {
            EmptyState(
                icon = Icons.Default.History,
                title = "Sin actividad",
                message = "Aún no hay acciones registradas en el sistema."
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(registros, key = { it.id }) { registro ->
                    AppCard {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            IconBadge(
                                icon = if (registro.accionRealizada.startsWith("CREA")) Icons.Default.Add else Icons.Default.Visibility
                            )
                            Spacer(modifier = Modifier.width(14.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = registro.accionRealizada,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Documento #${registro.documentoId} · Usuario #${registro.usuarioId}",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                if (registro.detalles != null) {
                                    Text(
                                        text = registro.detalles,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                                Text(
                                    text = registro.fechaHora,
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}