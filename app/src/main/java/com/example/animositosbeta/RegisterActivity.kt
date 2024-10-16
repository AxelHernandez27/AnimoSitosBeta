package com.example.animositosbeta

import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class RegisterActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registrar) // Asegúrate de que este sea el nombre correcto del layout

        // Obtener referencias a los elementos de la UI
        val emailEditText: EditText = findViewById(R.id.email)
        val phoneNumberEditText: EditText = findViewById(R.id.phone_number)
        val passwordEditText: EditText = findViewById(R.id.password)
        val confirmPasswordEditText: EditText = findViewById(R.id.confirm_password)
        val privacyPolicyCheckBox: CheckBox = findViewById(R.id.privacy_policy)
        val newsUpdatesCheckBox: CheckBox = findViewById(R.id.news_updates)
        val nextButton: Button = findViewById(R.id.btn_siguiente)

        // Configurar el listener del botón "Siguiente"
        nextButton.setOnClickListener {
            // Validar los campos de entrada
            val email = emailEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

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

            // Aquí puedes agregar la lógica para continuar con el registro
            // Por ejemplo, iniciar otra actividad o guardar los datos

            Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
            // Puedes iniciar una nueva actividad o realizar cualquier otra acción aquí
        }
    }
}
