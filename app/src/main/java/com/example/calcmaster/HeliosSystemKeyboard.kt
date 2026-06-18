package com.example.calcmaster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun HeliosSystemKeyboard(
    onInputText: (String) -> Unit,
    onBackspace: () -> Unit,
    onPanic: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                // Usamos el color negro de tu temática original
                .background(Color(0xFF0D0D0D)) 
                .padding(8.dp)
        ) {
            // Esta es tu misma lista de filas y botones de antes
            val rows = listOf(
                listOf("7", "8", "9", "/"),
                listOf("4", "5", "6", "*"),
                listOf("1", "2", "3", "-"),
                listOf(".", "0", "⌫", "+")
            )

            rows.forEach { row ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    row.forEach { label ->
                        // Reutilizamos tu componente HeliosButton original
                        HeliosButton(
                            label = label,
                            modifier = Modifier.weight(1f),
                            isOperator = label in listOf("/", "*", "-", "+"),
                            onClick = {
                                // AQUÍ ESTÁ EL TRUCO: 
                                // Si presionas la flecha de borrar, ejecuta borrar.
                                // Si presionas cualquier otro número, lo escribe en la otra app.
                                when (label) {
                                    "⌫" -> onBackspace()
                                    else -> onInputText(label)
                                }
                            }
                        )
                    }
                }
            }
        }
        // Mantenemos tus líneas de escaneo animadas (Scanlines) para la estética cyberpunk
        Scanlines()
    }
}
