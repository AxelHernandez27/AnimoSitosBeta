package com.example.animositosbeta

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class FeedbackActivity : AppCompatActivity() {

    private lateinit var firestore: FirebaseFirestore
    private lateinit var feedbackAdapter: FeedbackAdapter
    private lateinit var recyclerView: RecyclerView
    private val feedbackList = mutableListOf<Feedback>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calificar)

        firestore = FirebaseFirestore.getInstance() // Inicializa Firestore
        recyclerView = findViewById(R.id.recyclerViewComments)

        // Obteniendo doctorId del intent
        val doctorId = intent.getStringExtra("DOCTOR_ID")
        if (doctorId == null) {
            Toast.makeText(this, "Doctor ID no recibido", Toast.LENGTH_SHORT).show()
            return // Termina aquí si doctorId no está presente
        } else {
            Toast.makeText(this, "Doctor ID recibido: $doctorId", Toast.LENGTH_SHORT).show()
        }

        val doctorName = intent.getStringExtra("DOCTOR_NAME")
        val doctorLastName = intent.getStringExtra("DOCTOR_LASTNAME")
        val doctorGender = intent.getStringExtra("DOCTOR_GENDER")

        // Configura los datos en los elementos de la interfaz
        val nameTextView: TextView = findViewById(R.id.doctorNameText)
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val commentEditText: EditText = findViewById(R.id.comentarioText)
        val commentButton: Button = findViewById(R.id.comentarButton)

        nameTextView.text = if (doctorGender == "masculino") "Dr. $doctorName $doctorLastName" else "Dra. $doctorName $doctorLastName"

        // Configurar RecyclerView
        feedbackAdapter = FeedbackAdapter(feedbackList)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = feedbackAdapter

        // Cargar comentarios de Firestore
        loadFeedback(doctorId)

        // Manejar el clic del botón de comentar
        commentButton.setOnClickListener {
            val comment = commentEditText.text.toString()
            val rating = ratingBar.rating.toInt()

            if (comment.isNotEmpty()) {
                val feedback = Feedback(comment = comment, doctorId = doctorId, rating = rating)
                saveFeedback(feedback, doctorId)
                commentEditText.text.clear() // Limpiar el campo de texto
                ratingBar.rating = 0f // Reiniciar la calificación
            } else {
                Toast.makeText(this, "Por favor, escribe un comentario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFeedback(feedback: Feedback, doctorId: String) {
        firestore.collection("feedbacks")
            .add(feedback)
            .addOnSuccessListener {
                Toast.makeText(this, "Comentario enviado.", Toast.LENGTH_SHORT).show()
                loadFeedback(doctorId) // Recargar la lista de comentarios después de agregar uno nuevo
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar el comentario.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadFeedback(doctorId: String) {
        firestore.collection("feedbacks")
            .whereEqualTo("doctorId", doctorId)
            .get()
            .addOnSuccessListener { result ->
                feedbackList.clear()
                for (document in result) {
                    val comment = document.getString("comment") ?: ""
                    val doctorId = document.getString("doctorId") ?: ""
                    val rating = (document.get("rating") as? Number)?.toInt() ?: 0 // Validación de tipo
                    Log.d("FeedbackDebug", "Comentario: $comment, Rating: $rating")

                    // Crea el objeto Feedback y lo agrega a la lista
                    val feedback = Feedback(comment = comment, doctorId = doctorId, rating = rating)
                    feedbackList.add(feedback)
                }
                feedbackAdapter.notifyDataSetChanged() // Notificar cambios al adaptador
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar comentarios.", Toast.LENGTH_SHORT).show()
            }
    }
}
