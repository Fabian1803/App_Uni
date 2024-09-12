package com.example.appuni.ui.chat

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
        val root: View = binding.root

        // Configura los elementos de la interfaz de usuario o los observadores en el ViewModel
        val textView: TextView = binding.textChat
        chatViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
