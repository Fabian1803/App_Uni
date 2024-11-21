package com.example.appuni.ui.chat

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ScrollView
import android.widget.TextView
import androidx.fragment.app.activityViewModels
import com.example.appuni.R
import com.example.appuni.ui.SharedViewModel
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.appuni.data.Retrofit.RetrofitClient
import com.example.appuni.data.entities.Messages
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class ChatPanel : Fragment() {
    private var receiverId: Long? = null
    private var receiverName: String? = null
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private lateinit var messagesContainer: LinearLayout
    private lateinit var editText: EditText
    private lateinit var sendButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            receiverId = it.getLong("receiverId")
            receiverName = it.getString("receiverName")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_chat_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        messagesContainer = view.findViewById(R.id.messagesContainer)
        val nameTextView: TextView = view.findViewById(R.id.textView9)
        nameTextView.text = receiverName
        editText = view.findViewById(R.id.editTextText3)
        sendButton = view.findViewById(R.id.button)

        // Ahora observa el studentId en onViewCreated
        sharedViewModel.studentId.observe(viewLifecycleOwner) { studentId ->
            studentId?.let {
                receiverId?.let { receiverId ->
                    getMessages(it, receiverId)
                }
            }
        }

        sendButton.setOnClickListener {
            val messageText = editText.text.toString()
            if (messageText.isNotEmpty()) {
                sendMessage(messageText)
                editText.text.clear()  // Limpiar el campo de texto
            } else {
                Toast.makeText(context, "No puedes enviar un mensaje vacío", Toast.LENGTH_SHORT).show()
            }
        }

        scrollToBottom()
    }

    // Llamamos esta función para agregar un mensaje para hacer scroll al final.
    private fun scrollToBottom() {
        val scrollView: ScrollView = view?.findViewById(R.id.scrollView) ?: return
        scrollView.postDelayed({
            scrollView.fullScroll(View.FOCUS_DOWN)
        }, 100)
    }

    private fun getMessages(senderId: Long, receiverId: Long) {
        Log.d("ChatPanel", "Getting messages: senderId = $senderId, receiverId = $receiverId")

        lifecycleScope.launch(Dispatchers.IO) {
            val call = RetrofitClient.apiService.getMessagesBetween(senderId, receiverId)
            call.enqueue(object : Callback<List<Messages>> {
                override fun onResponse(call: Call<List<Messages>>, response: Response<List<Messages>>) {
                    if (response.isSuccessful) {
                        response.body()?.forEach { message ->
                            addMessagesView(message)
                        }
                    } else {
                        Log.e("ChatPanel", "Error: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Error al obtener mensajes", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Messages>>, t: Throwable) {
                    Log.e("ChatPanel", "Error de conexión: ${t.message}")
                    Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }


    private fun addMessagesView(message: Messages) {
        val currentUserId = sharedViewModel.studentId.value ?: return

        // Inflar el layout correcto según quién envía el mensaje
        val messageLayout = if (message.senderId == currentUserId) {
            layoutInflater.inflate(R.layout.transmitter_text, messagesContainer, false)
        } else {
            layoutInflater.inflate(R.layout.receiver_text, messagesContainer, false)
        }

        // Obtener las vistas
        val nameText: TextView = messageLayout.findViewById(if (message.senderId == currentUserId) R.id.text_nom_chat_i else R.id.text_nom_chat)
        val textogeneral: TextView = messageLayout.findViewById(if (message.senderId == currentUserId) R.id.text_chat_gn_i else R.id.text_chat_gn)
        val timeText: TextView = messageLayout.findViewById(if (message.senderId == currentUserId) R.id.text_date_chat_i else R.id.text_date_chat)

        // Asignar valores
        nameText.text = if (message.senderId == currentUserId) "Tú" else receiverName ?: "Nombre desconocido"
        textogeneral.text = message.messageText
        Log.d("ChatPanel", "Timestamp received: '$message.timestamp'")

        timeText.text = formatTimestamp(message.timestamp)

        // Agregar la vista al contenedor de mensajes
        messagesContainer.addView(messageLayout)
        scrollToBottom()
    }

    private fun formatTimestamp(timestamp: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val outputFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
        return try {
            val date = inputFormat.parse(timestamp)
            if (date != null) {
                ", " + outputFormat.format(date)
            } else {
                "Error"
            }
        } catch (e: Exception) {
            Log.e("ChatPanel", "Error parsing timestamp: ${e.message}")
            "Error"
        }
    }


    private fun sendMessage(messageText: String) {
        val currentUserId = sharedViewModel.studentId.value ?: return
        val receiverId = this.receiverId ?: return

        // Crear el objeto del mensaje
        val message = Messages(
            senderId = currentUserId,
            receiverId = receiverId,
            messageText = messageText,
            timestamp = getCurrentTimestamp()
        )

        lifecycleScope.launch(Dispatchers.IO) {
            val call = RetrofitClient.apiService.sendMessage(message)
            call.enqueue(object : Callback<Messages> {  // Cambiar aquí a Callback<Messages>
                override fun onResponse(call: Call<Messages>, response: Response<Messages>) {
                    if (response.isSuccessful) {
                        // Mensaje enviado exitosamente, puedes agregar la vista del mensaje a la UI
                        addMessagesView(response.body()!!)
                    } else {
                        Log.e("ChatPanel", "Error al enviar el mensaje: ${response.errorBody()?.string()}")
                        Toast.makeText(context, "Error al enviar el mensaje", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Messages>, t: Throwable) {
                    Log.e("ChatPanel", "Error de conexión: ${t.message}")
                    Toast.makeText(context, "Error de conexión: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    private fun getCurrentTimestamp(): String {
        return SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).format(Date())
    }
}

