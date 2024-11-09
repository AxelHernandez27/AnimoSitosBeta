package com.example.animositosbeta

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class AudioActivity : AppCompatActivity() {

    private lateinit var audioRecyclerView: RecyclerView
    private lateinit var audioAdapter: AudioAdapter
    private val audioList = mutableListOf<Audio>()
    private val storageRef: StorageReference = FirebaseStorage.getInstance().reference.child("audios/")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.registros_audios)

        audioRecyclerView = findViewById(R.id.recyclerViewAudio)
        audioRecyclerView.layoutManager = LinearLayoutManager(this)

        // Configuración del adaptador con callbacks de edición y eliminación
        audioAdapter = AudioAdapter(
            this,
            audioList,
            onEditClick = { audio -> editAudio(audio) },
            onDeleteClick = { audio -> deleteAudio(audio) }
        )
        audioRecyclerView.adapter = audioAdapter

        // Cargar audios desde Firebase
        loadAudiosFromFirebase()
    }

    private fun loadAudiosFromFirebase() {
        // Referencia al almacenamiento en Firebase donde están los audios
        storageRef.listAll().addOnSuccessListener { listResult ->
            listResult.items.forEach { item ->
                item.downloadUrl.addOnSuccessListener { uri ->
                    // Suponiendo que el nombre del archivo de audio incluye el título y la fecha
                    val audioTitle = item.name.substringBeforeLast(".")
                    val dateTime = "Autor desconocido" // Esto debería extraerse o configurarse según el caso
                    val audioUrl = uri.toString()

                    // Crear el objeto Audio y añadirlo a la lista
                    val audio = Audio(title = audioTitle, dateTime = dateTime, audioUrl = audioUrl)
                    audioList.add(audio)
                    audioAdapter.notifyDataSetChanged()
                }
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Error al cargar audios", Toast.LENGTH_SHORT).show()
        }
    }

    private fun editAudio(audio: Audio) {
        val editText = EditText(this).apply {
            setText(audio.title)
            setSelection(text.length)
        }

        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Nombre del Audio")
            .setView(editText)
            .setPositiveButton("Guardar") { dialogInterface, _ ->
                val newTitle = editText.text.toString()
                if (newTitle.isNotEmpty()) {
                    val oldAudioRef = FirebaseStorage.getInstance().getReferenceFromUrl(audio.audioUrl)
                    //val fileExtension = audio.audioUrl.substringAfterLast(".", "")
                    val newFileName = newTitle
                    val newAudioRef = storageRef.child(newFileName)

                    oldAudioRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                        newAudioRef.putBytes(bytes).addOnSuccessListener {
                            oldAudioRef.delete().addOnCompleteListener { task ->

                                if (task.isSuccessful) {
                                    // Limpia los parámetros adicionales de la URL antes de actualizarla
                                    val cleanUrl = newAudioRef.toString().substringBefore("?")

                                    audio.title = newFileName
                                    audio.audioUrl = cleanUrl  // Actualiza la URL del audio sin los parámetros
                                    audioAdapter.notifyDataSetChanged()

                                    Toast.makeText(this, "Nombre actualizado a $newFileName", Toast.LENGTH_SHORT).show()
                                } else {
                                    Toast.makeText(this, "Error al eliminar el archivo antiguo", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }.addOnFailureListener {
                            Toast.makeText(this, "Error al subir el archivo con el nuevo nombre", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener { exception ->
                        Log.e("AudioActivity", "Error al descargar el archivo: ${exception.message}")
                        Toast.makeText(this, "Error al descargar el archivo.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                dialogInterface.dismiss()
            }
            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    private fun deleteAudio(audio: Audio) {
        val audioRef = FirebaseStorage.getInstance().getReferenceFromUrl(audio.audioUrl)
        audioRef.delete().addOnSuccessListener {
            audioList.remove(audio)
            audioAdapter.notifyDataSetChanged()
            Toast.makeText(this, "${audio.title} eliminado", Toast.LENGTH_SHORT).show()
        }.addOnFailureListener { exception ->
            Log.e("AudioActivity", "Error al eliminar el archivo: ${exception.message}")
            Toast.makeText(this, "Error al eliminar ${audio.title}", Toast.LENGTH_SHORT).show()
        }
    }
}
