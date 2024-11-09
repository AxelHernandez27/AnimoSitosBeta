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
                    val dateTime = "Fecha desconocida" // Esto debería extraerse o configurarse según el caso
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
        // Crear un EditText dentro del cuadro de diálogo
        val editText = EditText(this).apply {
            setText(audio.title)  // Coloca el nombre actual del audio en el EditText
            setSelection(text.length)  // Coloca el cursor al final del texto
        }

        // Crear el cuadro de diálogo
        val dialog = AlertDialog.Builder(this)
            .setTitle("Editar Nombre del Audio")
            .setView(editText)  // Agrega el EditText al diálogo
            .setPositiveButton("Guardar") { dialogInterface, _ ->
                val newTitle = editText.text.toString()
                if (newTitle.isNotEmpty()) {
                    // Codificar el nombre del archivo para manejar espacios y caracteres especiales
                    val encodedFileName = Uri.encode(audio.title)
                    val fileExtension = audio.title.substringAfterLast(".", "")
                    val newFileName = "$newTitle.$fileExtension" // Nuevo nombre con la misma extensión

                    // Referencia al archivo original (sin codificar el nombre)
                    val oldAudioRef = storageRef.child("audios/$encodedFileName")

                    // Verificar si el archivo original existe
                    oldAudioRef.metadata.addOnSuccessListener {
                        Log.d("AudioActivity", "Archivo encontrado: ${audio.title}")
                        // El archivo existe, ahora descargamos los bytes
                        oldAudioRef.getBytes(Long.MAX_VALUE).addOnSuccessListener { bytes ->
                            // Subimos el archivo con el nuevo nombre
                            val newAudioRef = storageRef.child("audios/$newFileName") // Nueva ruta con el nombre actualizado
                            newAudioRef.putBytes(bytes).addOnSuccessListener {
                                // Si la carga es exitosa, eliminamos el archivo original
                                oldAudioRef.delete().addOnCompleteListener { task ->
                                    if (task.isSuccessful) {
                                        // Actualizar el título en la lista y notificar al adaptador
                                        audio.title = newFileName
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
                            Toast.makeText(this, "Error al descargar el archivo. El archivo no existe.", Toast.LENGTH_SHORT).show()
                        }
                    }.addOnFailureListener {
                        Log.e("AudioActivity", "Error al verificar el archivo: ${audio.title}")
                        // El archivo no existe o no se puede acceder
                        Toast.makeText(this, "El archivo no existe en la ubicación especificada.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show()
                }
                dialogInterface.dismiss()  // Cerrar el diálogo
            }
            .setNegativeButton("Cancelar") { dialogInterface, _ ->
                dialogInterface.dismiss()  // Cerrar el diálogo
            }
            .create()

        dialog.show()  // Mostrar el cuadro de diálogo
    }

    private fun deleteAudio(audio: Audio) {
        // Codificar el nombre del archivo para manejar espacios y caracteres especiales
        val encodedFileName = Uri.encode(audio.title)
        // Asegurarse de que la ruta del archivo sea correcta
        val audioRef = storageRef.child("audios/$encodedFileName") // Ruta completa del archivo
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
