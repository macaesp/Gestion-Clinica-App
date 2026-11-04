package com.example.gestionclinica.data

import androidx.room.TypeConverter
import org.json.JSONArray

class Converters {
    @TypeConverter
    fun listaAJson(lista: List<String>): String = JSONArray(lista).toString()

    @TypeConverter
    fun jsonALista(json: String): List<String> {
        if (json.isBlank()) return emptyList()
        val arreglo = JSONArray(json)
        return List(arreglo.length()) { arreglo.getString(it) }
    }
}