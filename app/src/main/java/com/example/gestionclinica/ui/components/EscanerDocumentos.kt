package com.example.gestionclinica.ui.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.platform.LocalContext
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

@Composable
fun rememberEscanerDocumento(
    onResultado: (paginas: List<String>, pdf: String?) -> Unit,
    onError: (String) -> Unit
): () -> Unit {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val alResultado = rememberUpdatedState(onResultado)
    val alError = rememberUpdatedState(onError)

    val launcher = rememberLauncherForActivityResult(
        ActivityResultContracts.StartIntentSenderForResult()
    ) { resultado ->
        // Si la persona cancela el escáner, no se hace nada.
        if (resultado.resultCode == Activity.RESULT_OK) {
            val datos = GmsDocumentScanningResult.fromActivityResultIntent(resultado.data)
            if (datos == null) {
                alError.value("No se recibió ningún documento del escáner.")
            } else {
                val imagenes = datos.pages?.map { it.imageUri } ?: emptyList()
                val pdf = datos.pdf?.uri
                scope.launch {
                    try {
                        val (paginas, rutaPdf) = withContext(Dispatchers.IO) {
                            val nombreBase = System.currentTimeMillis()
                            val paginasCopiadas = imagenes.mapIndexed { i, uri ->
                                copiarAAlmacenamientoInterno(context, uri, "${nombreBase}_p${i + 1}.jpg")
                            }
                            val pdfCopiado = pdf?.let {
                                copiarAAlmacenamientoInterno(context, it, "$nombreBase.pdf")
                            }
                            paginasCopiadas to pdfCopiado
                        }
                        alResultado.value(paginas, rutaPdf)
                    } catch (e: Exception) {
                        alError.value("No se pudo guardar el documento escaneado.")
                    }
                }
            }
        }
    }

    return {
        val activity = context.buscarActivity()
        if (activity == null) {
            alError.value("No se pudo abrir el escáner.")
        } else {
            val opciones = GmsDocumentScannerOptions.Builder()
                .setGalleryImportAllowed(true)
                .setPageLimit(10)
                .setResultFormats(
                    GmsDocumentScannerOptions.RESULT_FORMAT_JPEG,
                    GmsDocumentScannerOptions.RESULT_FORMAT_PDF
                )
                .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
                .build()

            GmsDocumentScanning.getClient(opciones)
                .getStartScanIntent(activity)
                .addOnSuccessListener { intentSender ->
                    launcher.launch(IntentSenderRequest.Builder(intentSender).build())
                }
                .addOnFailureListener {
                    alError.value("No se pudo abrir el escáner. Revisa que Google Play services esté actualizado.")
                }
        }
    }
}

private fun copiarAAlmacenamientoInterno(context: Context, origen: Uri, nombre: String): String {
    val carpeta = File(context.filesDir, "escaneos").apply { mkdirs() }
    val destino = File(carpeta, nombre)
    context.contentResolver.openInputStream(origen)?.use { entrada ->
        destino.outputStream().use { salida -> entrada.copyTo(salida) }
    } ?: error("No se pudo leer el archivo escaneado")
    return Uri.fromFile(destino).toString()
}

private tailrec fun Context.buscarActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.buscarActivity()
    else -> null
}