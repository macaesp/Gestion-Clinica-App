package com.example.gestionclinica.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.gestionclinica.ui.detalle.DetalleDocumentoScreen
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

        // Pantalla principal
        composable("home") {

            HomeScreen(
                onRegistrarDocumento = {
                    navController.navigate("registro")
                }
            )
        }

        // Registro de documento
        composable("registro") {

            RegistroDocumentoScreen(
                viewModel = viewModel,
                onDocumentoGuardado = {
                    navController.navigate("detalle")
                }
            )
        }

        // Detalle del documento
        composable("detalle") {

            DetalleDocumentoScreen(
                viewModel = viewModel,
                onVolver = {
                    navController.popBackStack()
                }
            )
        }
    }
}