package com.example.appuni.ui.login

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.appuni.ui.login.ui.theme.AppUniTheme

class CodeVerify : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AppUniTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    CodeVerificationScreen(modifier = Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@Composable
fun CodeVerificationScreen(modifier: Modifier = Modifier) {
    var code by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    val validCode = "123456"  // Código válido para comparación

    fun onVerify() {
        if (code.isEmpty()) {
            errorMessage = "Por favor, ingresa el código."
        } else if (code == validCode) {
            successMessage = "Código correcto."
            errorMessage = null
        } else {
            errorMessage = "Código incorrecto."
            successMessage = null
        }
    }

    // Función para reenviar el código
    fun onResendCode() {
        successMessage = null
        errorMessage = "Código reenviado."
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Verificación del Código",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de texto para ingresar el código
        OutlinedTextField(
            value = code,
            onValueChange = { code = it },
            label = { Text("Ingresa tu código") },
            isError = errorMessage != null,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        errorMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }

        successMessage?.let {
            Text(text = it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Botón para reenviar el código
        Button(
            onClick = { onResendCode() },
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            Text(text = "Reenviar Código")
        }

        // Botón para verificar el código
        Button(
            onClick = { onVerify() },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Verificar Código")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CodeVerificationPreview() {
    AppUniTheme {
        CodeVerificationScreen()
    }
}
