package harshzynx.saathi.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun SafetyHelplineDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Immediate Support Available") },
        text = {
            Text("You are not alone. If you're feeling overwhelmed, please speak to a professional counsellor at Tele-MANAS (Government of India). They are available 24/7.")
        },
        confirmButton = {
            Button(
                onClick = {
                    makeHelplineCall(context)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
            ) {
                Text("Call 14416 Now")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("I'm Okay")
            }
        }
    )
}

fun makeHelplineCall(context: Context) {
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:14416")
    }
    context.startActivity(intent)
}
