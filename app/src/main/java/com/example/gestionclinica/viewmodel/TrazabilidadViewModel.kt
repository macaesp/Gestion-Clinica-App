package com.example.gestionclinica.viewmodel

import android.app.Application
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.gestionclinica.data.AppDatabase
import com.example.gestionclinica.model.RegistroTrazabilidad
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TrazabilidadViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getInstance(application).trazabilidadDao()
    private val usuarioDao = AppDatabase.getInstance(application).usuarioDao()

    // Este solo se actualiza cuando la BD cambie
    val historial = mutableStateListOf<RegistroTrazabilidad>()
    private val nombresUsuarios = mutableStateMapOf<Int, String>()

    init {
        viewModelScope.launch {
            nombresUsuarios.clear()
            nombresUsuarios.putAll(usuarioDao.listarTodos().associate { it.id to it.nombre })
        }
        viewModelScope.launch {
            dao.observarTodos().collect { registros ->
                historial.clear()
                historial.addAll(registros)
            }
        }
    }

    fun nombreUsuario(id: Int): String = nombresUsuarios[id] ?: "Usuario #$id"

    fun registrarAccion(
        documentoId: Int,
        usuarioId: Int,
        accion: String,
        detalles: String? = null
    ) {
        // Compatible con API 24
        val fechaActual = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

        val nuevoRegistro = RegistroTrazabilidad(
            id = 0, // lo asigna Room (autoincremental)
            documentoId = documentoId,
            usuarioId = usuarioId,
            fechaHora = fechaActual,
            accionRealizada = accion,
            detalles = detalles
        )
        viewModelScope.launch { dao.insertar(nuevoRegistro) }
    }
}
