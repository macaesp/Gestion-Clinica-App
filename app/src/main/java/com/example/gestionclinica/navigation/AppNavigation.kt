package com.example.gestionclinica.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionclinica.ui.detalle.DetalleDocumentoScreen
import com.example.gestionclinica.ui.documentos.ListaDocumentosScreen
import com.example.gestionclinica.ui.home.HomeScreen
import com.example.gestionclinica.ui.registro.RegistroDocumentoScreen
import com.example.gestionclinica.viewmodel.DocumentoViewModel

@Composable
fun AppNavigation(
    viewModel: DocumentoViewModel
) {

    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home"
    ) {

        // HOME
        composable("home") {

            HomeScreen(
                onRegistrarDocumento = {
                    navController.navigate("registro")
                },
                onVerDocumentos = {
                    navController.navigate("documentos")
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

        // LISTA DE DOCUMENTOS
        composable("documentos") {

            ListaDocumentosScreen(
                viewModel = viewModel,

                onDocumentoClick = { documento ->

                    // Guardamos cuál documento seleccionó el usuario
                    viewModel.seleccionarDocumento(documento)

                    // Abrimos su detalle
                    navController.navigate("detalle")
                },

                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}