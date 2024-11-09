package com.example.animositosbeta

import android.content.Context
import android.media.MediaPlayer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AudioAdapter(
    private val context: Context,
    private val audioList: List<Audio>,
    private val onEditClick: (Audio) -> Unit,
    private val onDeleteClick: (Audio) -> Unit
) : RecyclerView.Adapter<AudioAdapter.AudioViewHolder>() {

    private var mediaPlayer: MediaPlayer? = null
    private var currentPlayingPosition: Int = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AudioViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.audio_item_layout, parent, false)
        return AudioViewHolder(view)
    }

    override fun onBindViewHolder(holder: AudioViewHolder, position: Int) {
        val audio = audioList[position]
        Log.d("AudioAdapter", "Audio cargado: $audio")
        holder.bind(audio)

        // Configuración del botón de reproducción
        holder.playButton.setOnClickListener {
            if (currentPlayingPosition == position) {
                stopAudio()
            } else {
                playAudio(audio, holder, position)
            }
        }

        // Configuración del botón de editar
        holder.editButton.setOnClickListener {
            onEditClick(audio)
        }

        // Configuración del botón de eliminar
        holder.deleteButton.setOnClickListener {
            onDeleteClick(audio)
        }
    }

    override fun getItemCount(): Int = audioList.size

    private fun playAudio(audio: Audio, holder: AudioViewHolder, position: Int) {
        stopAudio()
        mediaPlayer = MediaPlayer().apply {
            setDataSource(audio.audioUrl)
            prepare()
            start()
        }
        currentPlayingPosition = position
        holder.updateSeekBar()
    }

    private fun stopAudio() {
        mediaPlayer?.release()
        mediaPlayer = null
        currentPlayingPosition = -1
    }

    inner class AudioViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val audioTitle: TextView = itemView.findViewById(R.id.audioTitle)
        val audioDateTime: TextView = itemView.findViewById(R.id.audioDateTime)
        val playButton: ImageButton = itemView.findViewById(R.id.btnPlayPause)
        val editButton: ImageButton = itemView.findViewById(R.id.btnEdit)
        val deleteButton: ImageButton = itemView.findViewById(R.id.btnDelete)
        private val seekBar: SeekBar = itemView.findViewById(R.id.seekBarAudio)

        fun bind(audio: Audio) {
            audioTitle.text = audio.title
            audioDateTime.text = audio.dateTime
            seekBar.progress = 0
        }

        fun updateSeekBar() {
            mediaPlayer?.let { player ->
                seekBar.max = player.duration
                val handler = android.os.Handler()
                val runnable = object : Runnable {
                    override fun run() {
                        try {
                            seekBar.progress = player.currentPosition
                            handler.postDelayed(this, 500)
                        } catch (e: Exception) {
                            seekBar.progress = 0
                        }
                    }
                }
                handler.post(runnable)

                seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                        if (fromUser) {
                            player.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(seekBar: SeekBar?) {}
                    override fun onStopTrackingTouch(seekBar: SeekBar?) {}
                })
            }
        }
    }

    override fun onViewRecycled(holder: AudioViewHolder) {
        super.onViewRecycled(holder)
        if (currentPlayingPosition == holder.adapterPosition) {
            stopAudio()
        }
    }
}
