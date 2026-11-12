package com.example.gestionclinica.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.gestionclinica.ui.components.esFuncionario
import com.example.gestionclinica.ui.components.puedeGestionarFuncionarios
import com.example.gestionclinica.ui.components.puedeRegistrarDocumentos
import com.example.gestionclinica.ui.components.puedeModificarDocumentos
import com.example.gestionclinica.ui.components.puedeVerAuditoria
import com.example.gestionclinica.ui.screens.*
import com.example.gestionclinica.viewmodel.AuthViewModel
import com.example.gestionclinica.viewmodel.DocumentoViewModel
import com.example.gestionclinica.viewmodel.FuncionarioViewModel
import com.example.gestionclinica.viewmodel.TrazabilidadViewModel
import com.example.gestionclinica.model.RolUsuario
import com.example.gestionclinica.security.PoliticaAcceso

private data class DestinoInferior(
    val ruta: String,
    val etiqueta: String,
    val icono: ImageVector
)

@Composable
fun AppNavigation(viewModel: DocumentoViewModel) {
    val navController = rememberNavController()
    val funcionarioViewModel: FuncionarioViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val trazabilidadViewModel: TrazabilidadViewModel = viewModel()

    val rol = authViewModel.usuarioActual?.rol
    val esFuncionario = rol.esFuncionario()
    val funcionarioIdPermitido =
        if (esFuncionario) authViewModel.funcionarioActualId else null
    val usuarioAutenticado = authViewModel.usuarioActual
    val todosLosFuncionarios = funcionarioViewModel.todosLosFuncionarios

    LaunchedEffect(usuarioAutenticado, todosLosFuncionarios, esFuncionario, funcionarioIdPermitido) {
        funcionarioViewModel.establecerUsuarioAutorizado(usuarioAutenticado)
        viewModel.establecerAutorizacion(usuarioAutenticado, todosLosFuncionarios)
        viewModel.restringirAFuncionario(funcionarioIdPermitido)
    }

    val puedeVerDocumento: (com.example.gestionclinica.model.Documento) -> Boolean = { documento ->
        PoliticaAcceso.puedeVerDocumento(
            usuarioAutenticado,
            documento,
            todosLosFuncionarios.firstOrNull { it.id == documento.funcionarioId }
        )
    }

    val destinos = buildList {
        add(DestinoInferior("home", "Inicio", Icons.Default.Home))

        if (!esFuncionario) {
            add(DestinoInferior("funcionarios", "Funcionarios", Icons.Default.People))
        }

        add(DestinoInferior("documentos", "Documentos", Icons.Default.Folder))

        if (rol.puedeVerAuditoria()) {
            add(DestinoInferior("trazabilidad", "Auditoría", Icons.Default.Security))
        }
    }

    val backStackEntry by navController.currentBackStackEntryAsState()
    val rutaActual = backStackEntry?.destination?.route
    val mostrarBarraInferior =
        rutaActual != null && destinos.any { it.ruta == rutaActual }

    fun irADestinoPrincipal(ruta: String) {
        navController.navigate(ruta) {
            popUpTo("home") { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    fun irARegistro() {
        viewModel.limpiarFormulario()
        navController.navigate("registro")
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        bottomBar = {
            if (mostrarBarraInferior) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    destinos.forEach { destino ->
                        NavigationBarItem(
                            selected = rutaActual == destino.ruta,
                            onClick = { irADestinoPrincipal(destino.ruta) },
                            icon = {
                                Icon(destino.icono, contentDescription = null)
                            },
                            label = {
                                Text(destino.etiqueta)
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedIconColor =
                                    MaterialTheme.colorScheme.onPrimaryContainer,
                                selectedTextColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "login",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("login") {
                LoginScreen(
                    authViewModel = authViewModel,
                    onLoginSuccess = {
                        navController.navigate("home") {
                            popUpTo("login") { inclusive = true }
                        }
                    }
                )
            }

            composable("home") {
                HomeScreen(
                    authViewModel = authViewModel,
                    onVerFuncionarios = {
                        irADestinoPrincipal("funcionarios")
                    },
                    onRegistrarDocumento = {
                        if (esFuncionario && funcionarioIdPermitido != null) {
                            viewModel.prepararRegistroParaFuncionario(funcionarioIdPermitido)
                            navController.navigate("registro")
                        } else {
                            irARegistro()
                        }
                    },
                    onVerDocumentos = {
                        irADestinoPrincipal("documentos")
                    },
                    onVerTrazabilidad = {
                        irADestinoPrincipal("trazabilidad")
                    },
                    onAdministrarCuentas = {
                        if (rol == RolUsuario.RECURSOS_HUMANOS) {
                            navController.navigate("gestionUsuarios")
                        }
                    },
                    onLogout = {
                        navController.navigate("login") {
                            popUpTo("home") { inclusive = true }
                        }
                    },
                    onVerPerfil = { navController.navigate("perfil") },
                    totalDocumentos =
                        viewModel.documentosVisibles(funcionarioIdPermitido).size,
                    documentosPendientes =
                        viewModel.documentosVisibles(funcionarioIdPermitido)
                            .count { it.estado == "Pendiente" },
                    esFuncionario = esFuncionario
                )
            }

            composable("gestionUsuarios") {
                if (rol == RolUsuario.RECURSOS_HUMANOS) {
                    GestionUsuariosScreen(
                        authViewModel = authViewModel,
                        onVolver = { navController.popBackStack() }
                    )
                } else {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }

            composable("perfil") {
                if (esFuncionario) {
                    PerfilFuncionarioScreen(
                        funcionario = todosLosFuncionarios.firstOrNull {
                            it.id == funcionarioIdPermitido
                        },
                        onVolver = { navController.popBackStack() }
                    )
                } else {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }

            composable("funcionarios") {
                if (esFuncionario) {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                } else {
                    FuncionariosScreen(
                        viewModel = funcionarioViewModel,
                        onFuncionarioClick = { funcionario ->
                            funcionarioViewModel.seleccionarFuncionario(funcionario)
                            navController.navigate("expediente")
                        },
                        onAgregar = if (rol.puedeGestionarFuncionarios()) {
                            {
                                funcionarioViewModel.limpiarFormularioNuevo()
                                navController.navigate("nuevoFuncionario")
                            }
                        } else {
                            null
                        }
                    )
                }
            }

            composable("nuevoFuncionario") {
                if (rol.puedeGestionarFuncionarios()) {
                    RegistroFuncionarioScreen(
                        viewModel = funcionarioViewModel,
                        onGuardado = { navController.popBackStack() },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable("expediente") {
                val funcionarioActual = funcionarioViewModel.funcionarioSeleccionado
                if (
                    funcionarioActual != null &&
                    PoliticaAcceso.puedeVerExpediente(usuarioAutenticado, funcionarioActual)
                ) {
                    ExpedienteScreen(
                        viewModel = funcionarioViewModel,
                        onVerDocumentos = {
                            navController.navigate("documentosFuncionario")
                        },
                        onRegistrarDocumento = {
                            val id = funcionarioViewModel.funcionarioSeleccionado?.id
                            if (id != null) {
                                viewModel.prepararRegistroParaFuncionario(id)
                            }
                            navController.navigate("registro")
                        },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable("documentosFuncionario") {
                if (!esFuncionario) {
                    ListaDocumentosScreen(
                        viewModel = viewModel,
                        funcionarioFiltroId =
                            funcionarioViewModel.funcionarioSeleccionado?.id,
                        documentosPermitidos = funcionarioViewModel.funcionarioSeleccionado
                            ?.let { viewModel.obtenerDocumentosPorFuncionario(it.id) }
                            .orEmpty(),
                        onDocumentoClick = { documento ->
                            if (puedeVerDocumento(documento)) {
                                viewModel.seleccionarDocumento(documento)
                                navController.navigate("detalle")
                            }
                        },
                        onVolver = { navController.popBackStack() }
                    )
                }
            }

            composable("registro") {
                if (rol.puedeRegistrarDocumentos()) {
                    RegistroDocumentoScreen(
                        viewModel = viewModel,
                        usuarioId = authViewModel.usuarioActual?.id ?: 0,
                        esFuncionario = esFuncionario,
                        funcionarioId = funcionarioIdPermitido,
                        onDocumentoGuardado = {
                            val idUsuario =
                                authViewModel.usuarioActual?.id ?: 0
                            val docNuevo = viewModel.documentoSeleccionado

                            if (docNuevo != null) {
                                trazabilidadViewModel.registrarAccion(
                                    documentoId = docNuevo.id,
                                    usuarioId = idUsuario,
                                    accion = "CREACIÓN",
                                    detalles =
                                        "Documento creado tipo: ${docNuevo.clasificacion::class.simpleName}"
                                )
                            }

                            navController.navigate("detalle") {
                                popUpTo("registro") { inclusive = true }
                            }
                        },
                        onDocumentoModificado = { cambios ->
                            val editado = viewModel.documentoSeleccionado
                            if (editado != null && cambios.isNotEmpty()) {
                                trazabilidadViewModel.registrarAccion(
                                    documentoId = editado.id,
                                    usuarioId = authViewModel.usuarioActual?.id ?: 0,
                                    accion = "MODIFICACIÓN",
                                    detalles = "Se modificó: ${cambios.joinToString(", ")}"
                                )
                            }
                            navController.popBackStack()
                        },
                        onVolver = { navController.popBackStack() }
                    )
                } else {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }

            composable("detalle") {
                val docActual = viewModel.documentoSeleccionado
                val idUsuario = authViewModel.usuarioActual?.id ?: 0

                LaunchedEffect(docActual?.id) {
                    if (docActual != null) {
                        trazabilidadViewModel.registrarAccion(
                            documentoId = docActual.id,
                            usuarioId = idUsuario,
                            accion = "CONSULTA",
                            detalles = "Documento visualizado"
                        )
                    }
                }

                val documentoPermitido = docActual != null && puedeVerDocumento(docActual)

                if (documentoPermitido) {
                    DetalleDocumentoScreen(
                        viewModel = viewModel,
                        historial = trazabilidadViewModel.historial
                            .filter { it.documentoId == docActual.id }
                            .reversed(),
                        nombreUsuario = trazabilidadViewModel::nombreUsuario,
                        mostrarHistorial = !esFuncionario,
                        puedeModificar = rol.puedeModificarDocumentos(),
                        onEditar = {
                            viewModel.prepararEdicion(docActual)
                            navController.navigate("registro")
                        },
                        onVolver = { navController.popBackStack() }
                    )
                } else {
                    navController.navigate("documentos") {
                        popUpTo("home")
                    }
                }
            }

            composable("documentos") {
                ListaDocumentosScreen(
                    viewModel = viewModel,
                    onDocumentoClick = { documento ->
                        if (puedeVerDocumento(documento)) {
                            viewModel.seleccionarDocumento(documento)
                            navController.navigate("detalle")
                        }
                    },
                    documentosPermitidos = viewModel.documentosVisibles(funcionarioIdPermitido),
                    soloFuncionarioId = funcionarioIdPermitido,
                    onRegistrar = if (rol.puedeRegistrarDocumentos()) {
                        {
                            if (esFuncionario && funcionarioIdPermitido != null) {
                                viewModel.prepararRegistroParaFuncionario(funcionarioIdPermitido)
                                navController.navigate("registro")
                            } else {
                                irARegistro()
                            }
                        }
                    } else {
                        null
                    }
                )
            }

            composable("trazabilidad") {
                if (rol.puedeVerAuditoria()) {
                    TrazabilidadScreen(
                        trazabilidadViewModel = trazabilidadViewModel
                    )
                } else {
                    navController.navigate("home") {
                        popUpTo("home") { inclusive = false }
                    }
                }
            }
        }
    }
}
