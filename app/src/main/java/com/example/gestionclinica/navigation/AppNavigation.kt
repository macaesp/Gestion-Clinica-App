package com.example.gestionclinica.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionclinica.ui.screens.*
import com.example.gestionclinica.viewmodel.AuthViewModel
import com.example.gestionclinica.viewmodel.DocumentoViewModel
import com.example.gestionclinica.viewmodel.FuncionarioViewModel
import com.example.gestionclinica.viewmodel.TrazabilidadViewModel

@Composable
fun AppNavigation(
    viewModel: DocumentoViewModel
) {
    val navController = rememberNavController()
    val funcionarioViewModel: FuncionarioViewModel = viewModel()
    val authViewModel: AuthViewModel = viewModel()
    val trazabilidadViewModel: TrazabilidadViewModel = viewModel() // NUEVO

    NavHost(navController = navController, startDestination = "login") {
        composable("login") {
            LoginScreen(
                authViewModel = authViewModel,
                onLoginSuccess = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                }
            )
        }
        composable("home") {
            HomeScreen(
                authViewModel = authViewModel,
                onVerFuncionarios = { navController.navigate("funcionarios") },
                onRegistrarDocumento = { navController.navigate("registro") },
                onVerDocumentos = { navController.navigate("documentos") },
                onVerTrazabilidad = { navController.navigate("trazabilidad") }, // NUEVA RUTA
                onLogout = { navController.navigate("login") { popUpTo(0) } }
            )
        }
        composable("funcionarios") {
            FuncionariosScreen(
                viewModel = funcionarioViewModel,
                onFuncionarioClick = { funcionario ->
                    funcionarioViewModel.seleccionarFuncionario(funcionario)
                    navController.navigate("expediente")
                },
                onVolver = { navController.popBackStack() }
            )
        }
        composable("expediente") {
            ExpedienteScreen(
                viewModel = funcionarioViewModel,
                onVerDocumentos = { navController.navigate("documentosFuncionario") },
                onRegistrarDocumento = {
                    val id = funcionarioViewModel.funcionarioSeleccionado?.id
                    if (id != null) { viewModel.prepararRegistroParaFuncionario(id) }
                    navController.navigate("registro")
                },
                onVolver = { navController.popBackStack() }
            )
        }
        composable("documentosFuncionario") {
            ListaDocumentosScreen(
                viewModel = viewModel,
                funcionarioFiltroId = funcionarioViewModel.funcionarioSeleccionado?.id,
                onDocumentoClick = { documento ->
                    viewModel.seleccionarDocumento(documento)
                    navController.navigate("detalle")
                },
                onVolver = { navController.popBackStack() }
            )
        }
        composable("registro") {
            RegistroDocumentoScreen(
                viewModel = viewModel,
                onDocumentoGuardado = {
                    // AQUÍ REGISTRAMOS LA CREACIÓN
                    val idUsuario = authViewModel.usuarioActual?.id ?: 0
                    val docNuevo = viewModel.documentoSeleccionado
                    if (docNuevo != null) {
                        trazabilidadViewModel.registrarAccion(
                            documentoId = docNuevo.id,
                            usuarioId = idUsuario,
                            accion = "CREACIÓN",
                            detalles = "Documento creado tipo: ${docNuevo.clasificacion::class.simpleName}"
                        )
                    }
                    navController.navigate("detalle")
                }
            )
        }
        composable("detalle") {
            // AQUÍ REGISTRAMOS LA CONSULTA (Lectura)
            val docActual = viewModel.documentoSeleccionado
            val idUsuario = authViewModel.usuarioActual?.id ?: 0

            LaunchedEffect(docActual?.id) { // Solo se dispara una vez al abrir el documento
                if (docActual != null) {
                    trazabilidadViewModel.registrarAccion(
                        documentoId = docActual.id,
                        usuarioId = idUsuario,
                        accion = "CONSULTA",
                        detalles = "Documento visualizado"
                    )
                }
            }

            DetalleDocumentoScreen(
                viewModel = viewModel,
                onVolver = { navController.popBackStack() }
            )
        }
        composable("documentos") {
            ListaDocumentosScreen(
                viewModel = viewModel,
                onDocumentoClick = { documento ->
                    viewModel.seleccionarDocumento(documento)
                    navController.navigate("detalle")
                },
                onVolver = { navController.popBackStack() }
            )
        }
        composable("trazabilidad") {
            TrazabilidadScreen(
                trazabilidadViewModel = trazabilidadViewModel,
                onVolver = { navController.popBackStack() }
            )
        }
    }
}