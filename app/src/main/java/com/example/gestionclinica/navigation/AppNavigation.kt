package com.example.gestionclinica.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionclinica.ui.screens.DetalleDocumentoScreen
import com.example.gestionclinica.ui.screens.ExpedienteScreen
import com.example.gestionclinica.ui.screens.FuncionariosScreen
import com.example.gestionclinica.ui.screens.HomeScreen
import com.example.gestionclinica.ui.screens.ListaDocumentosScreen
import com.example.gestionclinica.ui.screens.RegistroDocumentoScreen
import com.example.gestionclinica.viewmodel.DocumentoViewModel
import com.example.gestionclinica.viewmodel.FuncionarioViewModel

@Composable
fun AppNavigation(
    viewModel: DocumentoViewModel
) {

    val navController = rememberNavController()

    val funcionarioViewModel: FuncionarioViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // HOME
        composable("home") {

            HomeScreen(
                onVerFuncionarios = {
                    navController.navigate("funcionarios")
                },

                onRegistrarDocumento = {
                    navController.navigate("registro")
                },

                onVerDocumentos = {
                    navController.navigate("documentos")
                }
            )
        }

        // FUNCIONARIOS
        composable("funcionarios") {

            FuncionariosScreen(
                viewModel = funcionarioViewModel,

                onFuncionarioClick = { funcionario ->

                    // Guardamos el funcionario seleccionado
                    funcionarioViewModel.seleccionarFuncionario(funcionario)

                    // Abrimos su expediente
                    navController.navigate("expediente")
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // EXPEDIENTE DEL FUNCIONARIO
        composable("expediente") {

            ExpedienteScreen(
                viewModel = funcionarioViewModel,

                onVerDocumentos = {

                    // Desde el expediente mostramos
                    // solamente los documentos del funcionario.
                    navController.navigate("documentosFuncionario")
                },

                onRegistrarDocumento = {
                    navController.navigate("registro")
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // DOCUMENTOS DEL FUNCIONARIO
        composable("documentosFuncionario") {

            val funcionarioSeleccionado =
                funcionarioViewModel.funcionarioSeleccionado

            ListaDocumentosScreen(
                viewModel = viewModel,

                // Filtramos usando el funcionario seleccionado
                funcionarioFiltro = funcionarioSeleccionado?.nombre,

                onDocumentoClick = { documento ->

                    viewModel.seleccionarDocumento(documento)

                    navController.navigate("detalle")
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // REGISTRAR DOCUMENTO
        composable("registro") {

            RegistroDocumentoScreen(
                viewModel = viewModel,

                onDocumentoGuardado = {
                    navController.navigate("detalle")
                }
            )
        }

        // DETALLE DEL DOCUMENTO
        composable("detalle") {

            DetalleDocumentoScreen(
                viewModel = viewModel,

                onVolver = {
                    navController.popBackStack()
                }
            )
        }

        // LISTA GENERAL DE DOCUMENTOS
        composable("documentos") {

            ListaDocumentosScreen(
                viewModel = viewModel,

                // No enviamos funcionarioFiltro.
                // Por eso aquí aparecen TODOS.
                onDocumentoClick = { documento ->

                    viewModel.seleccionarDocumento(documento)

                    navController.navigate("detalle")
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}