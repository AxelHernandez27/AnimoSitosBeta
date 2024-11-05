package com.example.animositosbeta

import android.content.Context
import android.media.MediaPlayer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioAdapter( private val context: Context,
                    private val audioUrls: List<String>,
                    private val onAudioClick: (String) -> Unit) :
    RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null

    private var isPlaying = false
    private var currentSeekBar: SeekBar? = null
    private var currentButton: ImageButton? = null
    private var currentPosition = -1 // Controla la posición actual del audio

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.audio_item_layout, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audioUrl = audioUrls[position]
        holder.bind(audioUrl)

        holder.itemView.setOnClickListener {
            currentButton = holder.btnPlayPause
            currentSeekBar = holder.seekBarAudio
            if (isPlaying && currentPosition == position) {
                pauseAudio() // Si ya está reproduciendo, pausar
            } else {
                if (isPlaying) {
                    stopAudio() // Si está reproduciendo otra canción, detener
                }
                playAudio(audioUrl, holder.seekBarAudio, holder.btnPlayPause, position)
            }
        }
    }

    override fun getItemCount() = audioUrls.size

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val audioTitle: TextView = itemView.findViewById(R.id.audioTitle)
        val audioDateTime: TextView = itemView.findViewById(R.id.audioDateTime)
        val btnPlayPause: ImageButton = itemView.findViewById(R.id.btnPlayPause)
        val seekBarAudio: SeekBar = itemView.findViewById(R.id.seekBarAudio)

        fun bind(audioUrl: String) {
            audioTitle.text = "Título de Audio" // Aquí podrías reemplazar con datos reales
            audioDateTime.text = "Fecha: 01/01/2024 12:00 PM" // Aquí podrías reemplazar con datos reales

            // Configura el SeekBar para la duración del audio
            seekBarAudio.max = mediaPlayer?.duration ?: 0

            // Configura el SeekBar
            seekBarAudio.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                    if (fromUser) {
                        mediaPlayer?.seekTo(progress) // Mueve el audio a la posición seleccionada
                    }
                }

                override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                override fun onStopTrackingTouch(seekBar: SeekBar?) {}
            })

            btnPlayPause.setOnClickListener {
                if (isPlaying) {
                    pauseAudio()
                } else {
                    playAudio(audioUrl, seekBarAudio, btnPlayPause, adapterPosition)
                }
            }
        }
    }

    private fun playAudio(audioUrl: String, seekBar: SeekBar, button: ImageButton, position: Int) {
        mediaPlayer?.release() // Libera el MediaPlayer anterior si existe
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audioUrl)
            prepareAsync()
            setOnPreparedListener {
                start() // Comienza la reproducción
                this@AudioAdapter.isPlaying = true // Cambia el estado a reproduciendo
                this@AudioAdapter.currentPosition = position // Guarda la posición actual
                button.setImageResource(R.drawable.play_pause_24px) // Cambia al ícono de pausa
                updateSeekBar(seekBar) // Inicia la actualización del SeekBar
            }
            setOnCompletionListener {
                this@AudioAdapter.isPlaying = false // Cambia el estado a no reproduciendo
                this@AudioAdapter.currentPosition = -1 // Resetea la posición actual
                button.setImageResource(R.drawable.play_pause_24px) // Cambia al ícono de reproducción
                seekBar.progress = 0 // Reinicia la barra de progreso
            }
        }
    }

    private fun pauseAudio() {
        mediaPlayer?.let {
            if (it.isPlaying) {
                it.pause() // Pausa el audio
                isPlaying = false // Cambia el estado a no reproduciendo
                currentButton?.setImageResource(R.drawable.play_pause_24px) // Cambia al ícono de reproducción
            }
        }
    }

    private fun stopAudio() {
        mediaPlayer?.let {
            it.stop() // Detiene el audio
            isPlaying = false // Cambia el estado a no reproduciendo
            currentButton?.setImageResource(R.drawable.play_pause_24px) // Cambia al ícono de reproducción
        }
        currentSeekBar?.progress = 0 // Reinicia la barra de progreso
        currentPosition = -1 // Resetea la posición actual
    }

    private fun updateSeekBar(seekBar: SeekBar) {
        Thread {
            while (isPlaying) {
                try {
                    Thread.sleep(1000) // Actualiza cada segundo
                    mediaPlayer?.let {
                        seekBar.progress = it.currentPosition // Actualiza la barra de progreso
                    }
                } catch (e: InterruptedException) {
                    e.printStackTrace()
                }
            }
        }.start()
    }

    override fun onViewRecycled(holder: AudioViewHolder) {
        super.onViewRecycled(holder)
        mediaPlayer?.release() // Libera el MediaPlayer al reciclar la vista
        isPlaying = false // Resetea el estado de reproducción
        currentPosition = -1 // Resetea la posición actual
    }
}
