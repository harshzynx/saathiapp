package harshzynx.saathi.ui.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun ComplianceDialog(onAccept: () -> Unit) {
    AlertDialog(
        onDismissRequest = { /* Mandatory accept */ },
        title = { Text("Medical Disclaimer ⚖️") },
        text = {
            Text("Saathi is an emotional support and wellbeing tool. It is NOT a substitute for professional medical advice, diagnosis, or treatment. \n\nAlways seek the advice of your physician or other qualified health provider with any questions you may have regarding a medical condition. In case of emergency, call 14416 immediately.")
        },
        confirmButton = {
            Button(onClick = onAccept) {
                Text("I Understand & Accept")
            }
        }
    )
}
