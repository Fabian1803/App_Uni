package com.example.appuni.ui.chat

import android.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.appuni.databinding.FragmentChatBinding

class ChatFragment : Fragment() {

    private var _binding: FragmentChatBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Obtén el ViewModel para el fragmento
        val chatViewModel = ViewModelProvider(this).get(ChatViewModel::class.java)

        // Infla el layout usando ViewBinding
        _binding = FragmentChatBinding.inflate(inflater, container, false)

        val spinner: Spinner = binding.spinner
        val options = listOf("Mensajes leídos", "Mensajes no leídos", "Todos los mensajes")
        val adapter = ArrayAdapter(requireContext(), R.layout.simple_spinner_item, options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        val root: View = binding.root
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

//listOf("Mensajes leídos", "Mensajes no leídos", "Todos los mensajes" )