package com.example.animositosbeta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar)

        // Obtener referencias a los elementos de la UI
        val emailEditText: EditText = findViewById(R.id.email)
        val phoneNumberEditText: EditText = findViewById(R.id.phone_number)
        val passwordEditText: EditText = findViewById(R.id.password)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password)
        val privacyPolicyCheckBox: CheckBox = findViewById(R.id.privacy_policy)
        val newsUpdatesCheckBox: CheckBox = findViewById(R.id.news_updates)
        val nextButton: Button = findViewById(R.id.btn_siguiente)

        // Configurar Firestore
        val db = FirebaseFirestore.getInstance()

        // Configurar el listener del botón "Siguiente"
        nextButton.setOnClickListener {
            // Validar los campos de entrada
            val email = emailEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()
            val newsUpdates = newsUpdatesCheckBox.isChecked

            if (email.isEmpty() || phoneNumber.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (!privacyPolicyCheckBox.isChecked) {
                Toast.makeText(this, "Debes aceptar la política de privacidad", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear un mapa con los datos del usuario
            val userData = hashMapOf(
                "email" to email,
                "phone" to phoneNumber.toLongOrNull(),
                "password" to password,
                "news" to newsUpdates
            )

            // Guardar los datos en Firestore
            db.collection("users")
                .add(userData)
                .addOnSuccessListener { documentReference ->
                    //Obtiene el ID generado para el usuario
                    val userId = documentReference.id

                    Toast.makeText(this, "${userId}", Toast.LENGTH_SHORT).show()
                    // Puedes iniciar una nueva actividad o realizar cualquier otra acción aquí
                    val intent = Intent(this, Register2Activity::class.java)
                    intent.putExtra("USER_ID", userId)
                    startActivity(intent)
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
