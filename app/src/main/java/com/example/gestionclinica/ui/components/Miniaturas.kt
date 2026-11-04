package com.example.gestionclinica.ui.components

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

/** Fila horizontal con las páginas escaneadas (rutas de archivo como texto). */
@Composable
fun MiniaturasPaginas(
    paginas: List<String>,
    modifier: Modifier = Modifier,
    ancho: Dp = 96.dp,
    alto: Dp = 128.dp
) {
    val forma = MaterialTheme.shapes.medium
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(paginas) { indice, ruta ->
            Box {
                AsyncImage(
                    model = ruta,
                    contentDescription = "Página ${indice + 1}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(ancho, alto)
                        .clip(forma)
                        .border(1.dp, MaterialTheme.colorScheme.outlineVariant, forma)
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(6.dp),
                    shape = MaterialTheme.shapes.small,
                    color = MaterialTheme.colorScheme.surface
                ) {
                    Text(
                        text = "${indice + 1}",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                    )
                }
            }
        }
    }
}