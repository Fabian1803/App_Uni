package com.example.appuni.ui.chat

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.appuni.R
import com.example.appuni.data.Retrofit.RetrofitClient
import com.example.appuni.data.entities.Student
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class search_user : Fragment() {
    private lateinit var resultContainer: LinearLayout
    private lateinit var searchEditText: EditText
    private lateinit var noResultsView: View
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_search_user, container, false)
        resultContainer = view.findViewById(R.id.resultContainer)
        searchEditText = view.findViewById(R.id.editTextText2)
        // Inflar el layout de no resultados
        noResultsView = inflater.inflate(R.layout.no_users_layout, resultContainer, false)
        showNoResults()
        // Agregar un TextWatcher para detectar cambios en el EditText
        searchEditText.addTextChangedListener(object : TextWatcher {

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // nada
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Llamar a la búsqueda cuando el texto cambie
                val query = s.toString()
                if (query.length >= 2) {
                    searchStudents(query)
                } else {
                    showNoResults() // Limpiar resultados si hay menos de 2 caracteres
                }
            }

            override fun afterTextChanged(s: Editable?) {
                // nada
            }
        })
        return view
    }

    private fun searchStudents(query: String) {
        RetrofitClient.apiService.searchStudents(query).enqueue(object : Callback<List<Student>> {
            override fun onResponse(call: Call<List<Student>>, response: Response<List<Student>>) {
                if (response.isSuccessful) {
                    resultContainer.removeAllViews() // Limpiar resultados anteriores
                    response.body()?.forEach { student ->
                        addStudentView(student)
                    }
                } else {
                    showNoResults()
                }
            }

            override fun onFailure(call: Call<List<Student>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addStudentView(student: Student) {
        val studentInfoView = layoutInflater.inflate(R.layout.user_info_layout, resultContainer, false)

        val nombreTextView: TextView = studentInfoView.findViewById(R.id.nombreTextView)
        val codigoTextView: TextView = studentInfoView.findViewById(R.id.codigoTextView)
        val emailTextView: TextView = studentInfoView.findViewById(R.id.emailTextView)
        val cursoTextView: TextView = studentInfoView.findViewById(R.id.CursoTextView)

        nombreTextView.text = "${student.firstName} ${student.lastName}"
        codigoTextView.text = student.code
        emailTextView.text = student.email
        cursoTextView.text = student.career

        // Set OnClickListener para navegar al ChatFragment usando NavController
        studentInfoView.setOnClickListener {
            val receiverId = student.id // Asegúrate de que el ID esté disponible en el objeto Student
            val navController = findNavController() // Obtén el NavController
            val receiverName = "${student.firstName} ${student.lastName}"
            // Crear un bundle para pasar el ID
            val bundle = Bundle().apply {
                putLong("receiverId", receiverId)
                putString("receiverName", receiverName)
            }

            // Navega al chatPanel y pasa los argumentos
            navController.navigate(R.id.chatPanel, bundle)
        }

        resultContainer.addView(studentInfoView)
    }
    private fun showNoResults() {
        resultContainer.removeAllViews() // Limpiar resultados anteriores
        resultContainer.addView(noResultsView) // Mostrar el layout de "no resultados"
    }
}
