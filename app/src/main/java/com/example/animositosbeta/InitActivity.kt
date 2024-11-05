package com.example.animositosbeta

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class InitActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.inicio)

        val btnNotificaciones: Button = findViewById(R.id.btn_notificaciones)
        val btnDirectorio: Button = findViewById(R.id.btn_directorio)
        val btnAudios: Button = findViewById(R.id.btn_registro)
        btnNotificaciones.setOnClickListener {
            // Inicia la actividad de Notificaciones
            val intent = Intent(this, NotificationsActivity::class.java)
            startActivity(intent)
        }

        btnDirectorio.setOnClickListener {
            // Iniciar la actividad de Directorio
            val intent = Intent(this, DirectoryActivity::class.java)
            startActivity(intent)
        }

        btnAudios.setOnClickListener{
            val intent = Intent(this, AudioActivity::class.java)
            startActivity(intent)
        }

    }
}