package com.example.appuni.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import com.example.appuni.R
import androidx.lifecycle.lifecycleScope
import com.example.appuni.data.Retrofit.RetrofitClient
import com.example.appuni.data.entities.Course
import com.example.appuni.databinding.FragmentHomeBinding
import com.example.appuni.ui.SharedViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import androidx.navigation.fragment.findNavController

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val sharedViewModel: SharedViewModel by activityViewModels()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inicializar el Spinner
        val spinner: Spinner = binding.spinner

        // Crear una lista de opciones para el Spinner
        val options = listOf("Periodo Actual", "Otros")

        // Crear un adaptador para el Spinner
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        // Configurar el Spinner con el adaptador
        spinner.adapter = adapter

        // Obtener cursos usando el studentId
        sharedViewModel.studentId.observe(viewLifecycleOwner) { id ->
            id?.let {
                getCourses(it)
            }
        }
        return binding.root
    }

    private fun getCourses(studentId: Long) {

        lifecycleScope.launch(Dispatchers.IO) {
            val call = RetrofitClient.apiService.getCourses(studentId)
            call.enqueue(object : Callback<List<Course>> {
                override fun onResponse(call: Call<List<Course>>, response: Response<List<Course>>) {
                    if (response.isSuccessful) {
                        val courses = response.body()
                        if (courses != null) {
                            for (course in courses) {
                                addCourseView(course)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Error al obtener los cursos", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Course>>, t: Throwable) {
                    Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun addCourseView(course: Course) {
        val courseLayout = binding.courseLayout // Asegúrate de tener este LinearLayout en tu XML
        val courseView = layoutInflater.inflate(R.layout.course_item_layout, courseLayout, false)

        courseView.findViewById<TextView>(R.id.textView11).text = course.courseName
        courseView.findViewById<TextView>(R.id.textView12).text = course.courseCode
        courseView.findViewById<TextView>(R.id.textView16).text = course.teacher

        courseLayout.addView(courseView)
        courseView.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.coursePanel)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}