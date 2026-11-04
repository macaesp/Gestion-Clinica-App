package com.example.gestionclinica.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.example.gestionclinica.model.RegistroTrazabilidad
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrazabilidadViewModel : ViewModel() {

    val historial = mutableStateListOf<RegistroTrazabilidad>()

    fun registrarAccion(
        documentoId: Int,
        usuarioId: Int,
        accion: String,
        detalles: String? = null
    ) {
        // Obtenemos la fecha con compatibilidad para API 24
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val nuevoRegistro = RegistroTrazabilidad(
            id = historial.size + 1,
            documentoId = documentoId,
            usuarioId = usuarioId,
            fechaHora = fechaActual,
            accionRealizada = accion,
            detalles = detalles
        )
        historial.add(nuevoRegistro)
    }
}