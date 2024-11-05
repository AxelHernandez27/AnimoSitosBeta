package com.example.animositosbeta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        db = FirebaseFirestore.getInstance()

        val loginButton: Button = findViewById(R.id.login)
        loginButton.setOnClickListener {
            val emailEditText: EditText = findViewById(R.id.email)
            val passwordEditText: EditText = findViewById(R.id.password)

            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authenticateUser(email, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun authenticateUser(email: String, password: String) {
        db.collection("users")
            .whereEqualTo("email", email)
            .whereEqualTo("password", password)
            .get()
            .addOnSuccessListener { document ->
                if (document.isEmpty) {
                    Toast.makeText(this, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                } else {
                    val userID = document.documents[0].id
                    fetchParentID(userID) // Llama a la función para obtener el `parentID`
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al autenticar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchParentID(userID: String) {
        db.collection("parents")
            .whereEqualTo("userId", userID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val parentID = documents.documents[0].id
                    fetchKidName(parentID) // Obtiene el nombre del niño usando el `parentID`
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener parentID: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun fetchKidName(parentID: String) {
        db.collection("kids")
            .whereEqualTo("parentId", parentID)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    val kidName = documents.documents[0].getString("name") ?: ""
                    val intent = Intent(this, InitActivity::class.java).apply {
                        putExtra("kidName", kidName) // Pasa el nombre del niño
                    }
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al obtener el nombre del niño: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
