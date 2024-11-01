package com.example.animositosbeta

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class DoctorAdapter(private var doctorList: List<Doctor>, private val context: Context) :
    RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder>() {

    class DoctorViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val doctorButton: Button = view.findViewById(R.id.doctorButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DoctorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.doctor_item, parent, false)
        return DoctorViewHolder(view)
    }

    override fun onBindViewHolder(holder: DoctorViewHolder, position: Int) {
        val doctor = doctorList[position]
        holder.doctorButton.text = if (doctor.gender == "masculino") {
            "Dr. ${doctor.name} ${doctor.lastname}"
        } else {
            "Dra. ${doctor.name} ${doctor.lastname}"
        }

        // Manejo del click para redirigir a la pantalla de información del doctor
        holder.doctorButton.setOnClickListener {
            val intent = Intent(context, FeedbackActivity::class.java).apply {
                putExtra("DOCTOR_NAME", doctor.name)
                putExtra("DOCTOR_LASTNAME", doctor.lastname)
                putExtra("DOCTOR_GENDER", doctor.gender)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = doctorList.size

    fun updateData(newList: List<Doctor>) {
        doctorList = newList
        notifyDataSetChanged()
    }
}
