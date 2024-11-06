package com.example.animositosbeta

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class DirectoryActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var doctorAdapter: DoctorAdapter
    private lateinit var searchBar: EditText
    private val db = FirebaseFirestore.getInstance()
    private val doctorList = mutableListOf<Doctor>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.directorio)

        // Configuración del RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        doctorAdapter = DoctorAdapter(emptyList(), this)
        recyclerView.adapter = doctorAdapter

        // Configuración del campo de búsqueda
        searchBar = findViewById(R.id.search_bar)
        searchBar.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filterDoctors(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Cargar datos desde Firebase
        loadDoctorsFromDatabase()
    }

    private fun loadDoctorsFromDatabase() {
        db.collection("doctors")
            .get()
            .addOnSuccessListener { documents ->
                doctorList.clear()
                for (document in documents) {
                    val doctor = document.toObject(Doctor::class.java)
                    doctor.id = document.id  // Asignar el ID del documento de Firestore al campo `id` de Doctor
                    doctorList.add(doctor)
                }
                doctorAdapter.updateData(doctorList)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
            }
    }


    private fun filterDoctors(query: String) {
        val filteredList = doctorList.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.lastname.contains(query, ignoreCase = true)
        }
        doctorAdapter.updateData(filteredList)
    }
}
