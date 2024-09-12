package com.example.appuni.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appuni.R

class LoginFragment : Fragment() {

    // Credenciales estáticas (en una aplicación real, obtendrás esto de un servidor)
    private val validUsername = "u20227896"
    private val validPassword = "123456"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener referencias a los campos de entrada, el botón y el TextView
        val usernameEditText: EditText = view.findViewById(R.id.editTextText)
        val passwordEditText: EditText = view.findViewById(R.id.editTextTextPassword)
        val loginButton: Button = view.findViewById(R.id.btn_redirect)
        val recoverAccountTextView: TextView = view.findViewById(R.id.textView6)

        // Configurar el botón
        loginButton.setOnClickListener {
            // Obtener valores ingresados por el usuario
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            // Validar credenciales
            if (username == validUsername && password == validPassword) {
                // Credenciales válidas, redirigir al usuario
                findNavController().navigate(R.id.nav_home) // Cambia a tu destino deseado
            } else {
                // Credenciales inválidas, mostrar mensaje de error
                Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        // Configurar el TextView para redirigir a un enlace
        recoverAccountTextView.setOnClickListener {
            // Crear un Intent para abrir el navegador
            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://universidadsideralcarrion.com/Ayuda"))
            startActivity(intent)
        }
    }
}
