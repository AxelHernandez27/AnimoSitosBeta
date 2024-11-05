package com.example.animositosbeta

import android.os.Bundle
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
        loadFeedback()

        // Manejar el clic del botón de comentar
        commentButton.setOnClickListener {
            val comment = commentEditText.text.toString()
            val rating = ratingBar.rating.toInt()
            val doctorId = "someDoctorId" // Aquí deberías obtener el ID del doctor de manera adecuada

            if (comment.isNotEmpty()) {
                val feedback = Feedback(comment = comment, doctorId = doctorId, rating = rating)
                saveFeedback(feedback)
                commentEditText.text.clear() // Limpiar el campo de texto
                ratingBar.rating = 0f // Reiniciar la calificación
            } else {
                Toast.makeText(this, "Por favor, escribe un comentario.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun saveFeedback(feedback: Feedback) {
        firestore.collection("feedbacks")
            .add(feedback)
            .addOnSuccessListener {
                Toast.makeText(this, "Comentario enviado.", Toast.LENGTH_SHORT).show()
                loadFeedback() // Recargar la lista de comentarios después de agregar uno nuevo
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al enviar el comentario.", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadFeedback() {
        firestore.collection("feedbacks")
            .get()
            .addOnSuccessListener { result ->
                feedbackList.clear()
                for (document in result) {
                    val feedback = document.toObject(Feedback::class.java)
                    feedbackList.add(feedback)
                }
                feedbackAdapter.notifyDataSetChanged() // Notificar al adaptador que los datos han cambiado
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al cargar comentarios.", Toast.LENGTH_SHORT).show()
            }
    }
}