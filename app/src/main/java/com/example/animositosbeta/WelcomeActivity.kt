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

        // Obtener la raíz del ConstraintLayout
        val rootView: View = findViewById(R.id.bienvenida)

        // Mostrar el mensaje de bienvenida
        welcomeText?.visibility = View.VISIBLE

        // Configurar un Handler para mostrar el mensaje principal después de 30 segundos
        Handler().postDelayed({
            welcomeText?.visibility = View.GONE
            mainMessage?.visibility = View.VISIBLE
        }, 30000) // 30000 ms (30 segundos)

        // Configurar otro Handler para mostrar el mensaje adicional después de 30 segundos más
        Handler().postDelayed({
            mainMessage?.visibility = View.GONE
            additionalMessage?.visibility = View.VISIBLE

            // Redirigir a la pantalla de inicio después de 60 segundos (1 minuto)
            Handler().postDelayed({
                val intent = Intent(this, InitActivity::class.java)
                startActivity(intent)
                finish() // Cerrar la actividad actual
            }, 60000) // 60000 ms (60 segundos)
        }, 60000) // 60000 ms (1 minuto)
    }
}
