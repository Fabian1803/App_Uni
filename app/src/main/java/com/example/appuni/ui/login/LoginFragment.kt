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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.appuni.R
import com.example.appuni.data.Retrofit.RetrofitClient
import com.example.appuni.data.entities.Student
import com.example.appuni.ui.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginFragment : Fragment() {

    private val sharedViewModel: SharedViewModel by activityViewModels()
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

            // Llamada a la API para autenticar
            authenticateStudent(username, password)

        }

        // Configurar el TextView para redirigir a un enlace
        recoverAccountTextView.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.passwordRecovery)
        }
    }
    private fun authenticateStudent(code: String, password: String) {
        lifecycleScope.launch(Dispatchers.IO) {
            val call = RetrofitClient.apiService.authenticateStudent(code, password)
            call.enqueue(object : Callback<Student> {
                override fun onResponse(call: Call<Student>, response: Response<Student>) {
                    if (response.isSuccessful) {
                        val student = response.body()
                        if (student != null) {
                            // Actualiza el studentId en el ViewModel compartido
                            sharedViewModel.studentId.postValue(student.id)

                            // Credenciales válidas, actualizar el encabezado en MainActivity
                            val mainActivity = activity as MainActivity
                            mainActivity.updateHeader(student.firstName, student.lastName, student.career)

                            // Navegar a la pantalla de inicio
                            findNavController().navigate(R.id.nav_home)
                        } else {
                            Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(context, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Student>, t: Throwable) {
                    Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }
}
