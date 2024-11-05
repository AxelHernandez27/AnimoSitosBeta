package com.example.animositosbeta

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NotificationAdapter(
    private var notificationList: List<Notification>,
    private val context: Context,
    private val kidName: String // Recibe el nombre del niño
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    class NotificationViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageTextView: TextView = view.findViewById(R.id.notification_message)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.notification_item, parent, false)
        return NotificationViewHolder(view)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification = notificationList[position]
        // Reemplaza "[nombre del niño]" con el valor de kidName
        val message = notification.message.replace("[nombre del niño]", kidName)
        holder.messageTextView.text = message
    }

    override fun getItemCount() = notificationList.size

    fun updateData(newList: List<Notification>) {
        notificationList = newList
        notifyDataSetChanged()
    }

}
