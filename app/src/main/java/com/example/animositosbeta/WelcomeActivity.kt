package com.example.animositosbeta

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class WelcomeActivity : AppCompatActivity() {

    private var additionalMessage: TextView? = null
    private var mainMessage: TextView? = null
    private var welcomeText: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.bienvenida) // Asegúrate de usar tu layout correcto

        additionalMessage = findViewById(R.id.additional_message)
        mainMessage = findViewById(R.id.main_message)
        welcomeText = findViewById(R.id.welcome_text)

        // Obtener el nombre del padre desde el Intent
        val parentName = intent.getStringExtra("PARENT_NAME") ?: "Usuario"
        welcomeText?.text = "Bienvenido, $parentName!" // Saludo personalizado

        // Mostrar el mensaje de bienvenida
        welcomeText?.visibility = View.VISIBLE

        // Configurar un Handler para mostrar el mensaje principal y redirigir después de 30 segundos
        Handler().postDelayed({
            // Mostrar el mensaje adicional
            welcomeText?.visibility = View.GONE
            mainMessage?.visibility = View.VISIBLE

            // Después de mostrar el mensaje principal, redirigir a InitActivity
            Handler().postDelayed({
                mainMessage?.visibility = View.GONE
                additionalMessage?.visibility = View.VISIBLE
                val intent = Intent(this, InitActivity::class.java)
                startActivity(intent)
                finish() // Cerrar la actividad actual
            }, 10000) // 10000 ms (10 segundos)
        }, 10000) // 10000 ms (10 segundos)
    }
}
