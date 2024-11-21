package com.example.appuni.ui.login

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.appuni.data.Retrofit.RetrofitClient
import com.example.appuni.data.entities.Student
import com.example.appuni.ui.login.ui.theme.AppUniTheme
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

class PasswordRecovery : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppUniTheme {
                NavHostScreen()
            }
        }
    }
}

@Composable
fun NavHostScreen() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "passwordRecoveryScreen") {
        composable("passwordRecoveryScreen") {
            PasswordRecoveryScreen(navController = navController)
        }
        composable("codeVerificationScreen") {
            CodeVerificationScreen()
        }
    }
}

@Composable
fun PasswordRecoveryScreen(navController: NavController, modifier: Modifier = Modifier) {
    val context = LocalContext.current  // Obtener el contexto
    var email by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }

    fun onSubmit() {
        if (email.isEmpty()) {
            errorMessage = "Por favor, ingresa tu correo."
        } else {
            loading = true
            errorMessage = null
            successMessage = null

            // Llamada a Retrofit para verificar si el correo existe
            RetrofitClient.apiService.verifyEmail(email).enqueue(object : Callback<Student> {
                override fun onResponse(call: Call<Student>, response: Response<Student>) {
                    loading = false
                    if (response.isSuccessful) {
                        val student = response.body()
                        if (student != null) {
                            val randomCode = generateRandomCode()
                            // Enviar el código por notificación
                            showNotification(context, "Tu código de recuperación es: $randomCode")
                            successMessage = "Correo encontrado, código enviado."
                            // Redirigir a la pantalla de verificación de código
                            navController.navigate("codeVerificationScreen")
                        } else {
                            errorMessage = "Correo no encontrado."
                        }
                    } else {
                        errorMessage = "Error al verificar el correo."
                    }
                }

                override fun onFailure(call: Call<Student>, t: Throwable) {
                    loading = false
                    errorMessage = "Error de conexión: ${t.message}"
                }
            })
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Recuperación de Contraseña",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Campo de texto
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo Universitario") },
            isError = errorMessage != null,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { /* Acción adicional si lo deseas */ }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Mostrar el mensaje de error
        errorMessage?.let {
            Text(text = it, color = Color.Red, modifier = Modifier.padding(top = 8.dp))
        }

        // Mostrar el mensaje
        successMessage?.let {
            Text(text = it, color = Color.Green, modifier = Modifier.padding(top = 8.dp))
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = { onSubmit() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !loading
        ) {
            if (loading) {
                CircularProgressIndicator(
                    color = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Text(text = "Enviar Código")
            }
        }
    }
}

// Función para generar un código
fun generateRandomCode(): String {
    val randomCode = Random.nextInt(100000, 999999)  // Genera un número aleatorio entre 100000 y 999999
    return randomCode.toString()
}

// Función para mostrar una notificación
fun showNotification(context: Context, message: String) {
    val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Para Android Oreo (API 26) o superior, se necesita un canal de notificaciones
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "default", "General", NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Canal para notificaciones generales"
        }
        notificationManager.createNotificationChannel(channel)

        // Notificación para API 26 y superior, especificando el canal
        val notification = Notification.Builder(context, "default")
            .setContentTitle("Recuperación de Contraseña")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(1, notification)
    } else {
        // Notificación para versiones anteriores a API 26
        val notification = Notification.Builder(context)
            .setContentTitle("Recuperación de Contraseña")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .build()

        notificationManager.notify(1, notification)
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewPasswordRecoveryScreen() {
    AppUniTheme {
        PasswordRecoveryScreen(navController = rememberNavController())
    }
}
