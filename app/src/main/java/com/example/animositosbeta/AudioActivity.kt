package com.example.animositosbeta

import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class AudioActivity : AppCompatActivity() {
    private lateinit var recyclerViewAudios: RecyclerView
    private lateinit var audioAdapter: AudioAdapter
    private var mediaPlayer: MediaPlayer? = null // MediaPlayer como nullable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.registros_audios)

        recyclerViewAudios = findViewById(R.id.recyclerViewAudio)
        recyclerViewAudios.layoutManager = LinearLayoutManager(this)

        // Carga los audios desde Firestore
        loadAudiosFromFirestore()
    }

    private fun loadAudiosFromFirestore() {
        FirebaseFirestore.getInstance().collection("audios")
            .get()
            .addOnSuccessListener { documents ->
                val audioUrls = mutableListOf<String>()
                for (document in documents) {
                    val audioUrl = document.getString("audioUrl")
                    if (audioUrl != null) {
                        audioUrls.add(audioUrl)
                    }
                }
                audioAdapter = AudioAdapter(this, audioUrls) { audioUrl ->
                    playAudio(audioUrl) // Reproduce el audio al hacer clic
                }

                recyclerViewAudios.adapter = audioAdapter
            }
            .addOnFailureListener { exception ->
                Log.e("AudioActivity", "Error al cargar audios: ", exception)
            }
    }

    private fun playAudio(audioUrl: String) {
        mediaPlayer?.release() // Libera el MediaPlayer anterior, si existe
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl) // Establece la fuente de audio
            prepareAsync() // Prepara el MediaPlayer de manera asíncrona
            setOnPreparedListener {
                start() // Comienza a reproducir el audio
            }
            setOnCompletionListener {
                release() // Libera el MediaPlayer al completar la reproducción
                mediaPlayer = null // Asegúrate de que sea null después de liberar
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release() // Libera los recursos del MediaPlayer si existe
        mediaPlayer = null // Asegúrate de que sea null después de liberar
    }
}
