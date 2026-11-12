package com.example.gestionclinica.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.example.gestionclinica.ui.components.AppCard
import com.example.gestionclinica.ui.components.IconBadge
import com.example.gestionclinica.ui.components.esFuncionario
import com.example.gestionclinica.ui.components.etiqueta
import com.example.gestionclinica.ui.components.puedeRegistrarDocumentos
import com.example.gestionclinica.ui.components.puedeVerAuditoria
import com.example.gestionclinica.viewmodel.AuthViewModel
import androidx.compose.material.icons.filled.ManageAccounts
import androidx.compose.material.icons.filled.AccountCircle
import com.example.gestionclinica.model.RolUsuario

private data class Acceso(
    val titulo: String,
    val descripcion: String,
    val icono: ImageVector,
    val onClick: () -> Unit
)

@Composable
fun HomeScreen(
    authViewModel: AuthViewModel,
    onVerFuncionarios: () -> Unit,
    onRegistrarDocumento: () -> Unit,
    onVerDocumentos: () -> Unit,
    onVerTrazabilidad: () -> Unit,
    onLogout: () -> Unit,
    onVerPerfil: () -> Unit,
    totalDocumentos: Int = 0,
    documentosPendientes: Int = 0,
    esFuncionario: Boolean = false,
    onAdministrarCuentas: () -> Unit
) {
    val usuario = authViewModel.usuarioActual
    val rol = usuario?.rol
    val esFuncionarioActual = esFuncionario || rol.esFuncionario()

    val accesos = buildList {
        if (esFuncionarioActual) {
            add(
                Acceso(
                    titulo = "Mi perfil",
                    descripcion = "Mis datos y expediente",
                    icono = Icons.Default.AccountCircle,
                    onClick = onVerPerfil
                )
            )
        }

        if (!esFuncionarioActual) {
            add(
                Acceso(
                    titulo = "Funcionarios",
                    descripcion = "Consultar expedientes",
                    icono = Icons.Default.People,
                    onClick = onVerFuncionarios
                )
            )
        }

        add(
            Acceso(
                titulo = if (esFuncionarioActual) "Mis documentos" else "Documentos",
                descripcion = "Ver documentación",
                icono = Icons.Default.Folder,
                onClick = onVerDocumentos
            )
        )

        if (rol.puedeRegistrarDocumentos()) {
            add(
                Acceso(
                    titulo = "Registrar",
                    descripcion = "Nuevo documento",
                    icono = Icons.Default.Add,
                    onClick = onRegistrarDocumento
                )
            )
        }

        if (rol.puedeVerAuditoria()) {
            add(
                Acceso(
                    titulo = "Auditoría",
                    descripcion = "Trazabilidad",
                    icono = Icons.Default.Security,
                    onClick = onVerTrazabilidad
                )
            )
        }

        if (rol == RolUsuario.RECURSOS_HUMANOS) {
            add(
                Acceso(
                    titulo = "Cuentas",
                    descripcion = "Crear y habilitar usuarios",
                    icono = Icons.Default.ManageAccounts,
                    onClick = onAdministrarCuentas
                )
            )
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CMQ Área Salud",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            IconButton(
                onClick = {
                    authViewModel.logout()
                    onLogout()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Cerrar sesión"
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Text(
                    text = "Bienvenido/a",
                    style = MaterialTheme.typography.bodyLarge
                )

                Text(
                    text = usuario?.nombre ?: "",
                    style = MaterialTheme.typography.headlineSmall
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = rol?.etiqueta() ?: "",
                    style = MaterialTheme.typography.labelLarge
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ResumenCard(
                valor = totalDocumentos.toString(),
                etiqueta = if (esFuncionarioActual) {
                    "Mis documentos"
                } else {
                    "Documentos registrados"
                },
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            ResumenCard(
                valor = documentosPendientes.toString(),
                etiqueta = "Pendientes",
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Accesos rápidos",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(12.dp))

        accesos.chunked(2).forEach { fila ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                fila.forEach { acceso ->
                    AccesoTile(
                        acceso = acceso,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                    )
                }

                if (fila.size == 1) {
                    Spacer(modifier = Modifier.weight(1f))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
        }

        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
private fun ResumenCard(
    valor: String,
    etiqueta: String,
    modifier: Modifier = Modifier
) {
    AppCard(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = valor,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = etiqueta,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun AccesoTile(
    acceso: Acceso,
    modifier: Modifier = Modifier
) {
    AppCard(
        modifier = modifier,
        onClick = acceso.onClick
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            IconBadge(icon = acceso.icono)

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = acceso.titulo,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )

            Text(
                text = acceso.descripcion,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
