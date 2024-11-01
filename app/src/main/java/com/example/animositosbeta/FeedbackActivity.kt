package com.example.animositosbeta

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class FeedbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.calificar)

        val doctorName = intent.getStringExtra("DOCTOR_NAME")
        val doctorLastName = intent.getStringExtra("DOCTOR_LASTNAME")
        val doctorGender = intent.getStringExtra("DOCTOR_GENDER")

        // Configura los datos en los elementos de la interfaz
        val nameTextView: TextView = findViewById(R.id.doctorNameText)
        val ratingBar: RatingBar = findViewById(R.id.ratingBar)
        val commentEditText: EditText = findViewById(R.id.comentarioText)
        //val doctorImage: ImageView = findViewById(R.id.doctorImage)

        nameTextView.text = if (doctorGender == "masculino") "Dr. $doctorName $doctorLastName" else "Dra. $doctorName $doctorLastName"

        // Configura el botón de comentar o cualquier otra lógica adicional
    }
}
