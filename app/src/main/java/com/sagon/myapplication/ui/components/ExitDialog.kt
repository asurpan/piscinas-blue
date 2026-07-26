package com.sagon.myapplication.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ExitDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = "Salir de la App") },
        text = { Text(text = "¿Estás seguro de que quieres cerrar la aplicación?") },
        confirmButton = {
            TextButton(onClick = onConfirm) {
                Text("Sí, salir")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("No, volver")
            }
        }
    )
}
