package com.example.appuni.ui.login

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.appuni.MainActivity
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.appuni.R
import com.example.appuni.data.bd.AppDatabase
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as AppCompatActivity).supportActionBar?.hide()
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
            lifecycleScope.launch {
                val db = AppDatabase.getDatabase(requireContext())
                val student = db.studentDao().getStudentByCredentials(username, password)

                if (student != null) {
                    // Credenciales válidas, actualizar el encabezado en MainActivity
                    val mainActivity = activity as MainActivity
                    mainActivity.updateHeader(student.firstName, student.lastName, student.career)

                    // Navegar a la pantalla de inicio
                    findNavController().navigate(R.id.nav_home)
                } else {
                    // Credenciales inválidas, mostrar mensaje de error
                    Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                }
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
