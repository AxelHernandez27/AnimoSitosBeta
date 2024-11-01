package com.example.animositosbeta

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore

class NotificationsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationList: MutableList<Notification> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notificaciones)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        notificationAdapter = NotificationAdapter(emptyList(), this)
        recyclerView.adapter = notificationAdapter

        loadNotifications()
    }

    private fun loadNotifications() {
        db.collection("notifications").get()
            .addOnSuccessListener { result ->
                notificationList.clear()
                for (document in result) {
                    val notification = document.toObject(Notification::class.java)
                    notificationList.add(notification)
                }
                notificationAdapter.updateData(notificationList)
            }
            .addOnFailureListener { exception ->
                Log.w("NotificationsActivity", "Error loading notifications.", exception)
            }
    }
}
