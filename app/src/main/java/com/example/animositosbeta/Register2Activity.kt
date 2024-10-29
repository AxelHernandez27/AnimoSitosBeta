package com.example.animositosbeta

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore

class Register2Activity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar2)

        // Obtener referencias a los elementos de la UI
        val parentNameEditText: EditText = findViewById(R.id.name)
        val parentAgeEditText: EditText = findViewById(R.id.age)
        val childNameEditText: EditText = findViewById(R.id.childname)
        val childGenderEditText: EditText = findViewById(R.id.gender)
        val childAgeEditText: EditText = findViewById(R.id.childage)
        val childDiseasesEditText: EditText = findViewById(R.id.diseases)
        val ingresarButton: Button = findViewById(R.id.btn_ingresar)

        // Obtener el userId del usuario registrado
        val userId = intent.getStringExtra("USER_ID") ?: ""

        //Configurar Firestore
        val db = FirebaseFirestore.getInstance()

        ingresarButton.setOnClickListener {
            // Validar y obtener datos de los campos de texto
            val parentName = parentNameEditText.text.toString()
            val parentAge = parentAgeEditText.text.toString().toIntOrNull()
            val childName = childNameEditText.text.toString()
            val childGender = childGenderEditText.text.toString()
            val childAge = childAgeEditText.text.toString().toIntOrNull()
            val childDiseases = childDiseasesEditText.text.toString()

            if (parentName.isEmpty() || parentAge == null || childName.isEmpty() || childGender.isEmpty() || childAge == null) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Crear el objeto Parent y guardar en Firestore
            val parentData = hashMapOf(
                "name" to parentName,
                "age" to parentAge,
                "userId" to userId
            )

            db.collection("parents")
                .add(parentData)
                .addOnSuccessListener { documentReference ->
                    val parentId = documentReference.id

                    // Crear el objeto Kid y guardar en Firestore
                    val kidData = hashMapOf(
                        "name" to childName,
                        "gender" to childGender,
                        "age" to childAge,
                        "illnesses" to childDiseases,
                        "parentId" to parentId
                    )

                    db.collection("kids")
                        .add(kidData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Datos guardados exitosamente", Toast.LENGTH_SHORT).show()
                            // Redirigir a la actividad de bienvenida
                            val intent = Intent(this, WelcomeActivity::class.java)
                            startActivity(intent)
                            finish() // Opcional: cierra esta actividad
                        }
                        .addOnFailureListener { e ->
                            Toast.makeText(this, "Error al guardar los datos del niño: ${e.message}", Toast.LENGTH_SHORT).show()
                        }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error al guardar los datos del padre: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

}
