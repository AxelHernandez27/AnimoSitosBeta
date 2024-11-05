package com.example.animositosbeta

import android.icu.util.Calendar
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Timer
import kotlin.concurrent.timerTask

class NotificationsActivity : AppCompatActivity() {

    private val db = FirebaseFirestore.getInstance()
    private lateinit var recyclerView: RecyclerView
    private lateinit var notificationAdapter: NotificationAdapter
    private var notificationList: MutableList<Notification> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.notificaciones)

        val kidName = intent.getStringExtra("kidName") ?: ""

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)

        notificationAdapter = NotificationAdapter(emptyList(), this, kidName) // Pasa kidName al adaptador
        recyclerView.adapter = notificationAdapter

        loadNotifications()
        scheduleHourlyNotifications()
    }

    private fun loadNotifications() {
        db.collection("notifications").get()
            .addOnSuccessListener { result ->
                notificationList.clear()

                // Filtrar solo una notificación de cada tipo
                val selectedNotifications = mutableListOf<Notification>()
                val typesShown = mutableSetOf<String>()

                for (document in result) {
                    val notification = document.toObject(Notification::class.java)
                    if (notification.type in setOf("cuidado", "cita", "completada", "motivacional") &&  notification.type !in typesShown) {
                        selectedNotifications.add(notification)
                        typesShown.add(notification.type)
                    }
                }
                notificationList.addAll(selectedNotifications)
                notificationAdapter.updateData(notificationList)
            }
            .addOnFailureListener { exception ->
                Log.w("NotificationsActivity", "Error loading notifications.", exception)
            }
    }

    private fun scheduleHourlyNotifications() {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 23)
            set(Calendar.MINUTE, 33)
            set(Calendar.SECOND, 0)
        }

        val now = Calendar.getInstance()
        if (now.after(calendar)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
        }

        val delay = calendar.timeInMillis - now.timeInMillis
        val timer = Timer()

        timer.schedule(timerTask {
            loadAdditionalCareNotifications()
        }, delay, 60000) // 3600000 ms = 1 hora
    }

    private fun loadAdditionalCareNotifications() {
        db.collection("notifications")
            .whereEqualTo("type", "cuidado")
            .get()
            .addOnSuccessListener { result ->
                val additionalCareNotifications = mutableListOf<Notification>()

                for (document in result) {
                    val notification = document.toObject(Notification::class.java)
                    additionalCareNotifications.add(notification)
                }

                runOnUiThread {
                    notificationList.addAll(additionalCareNotifications)
                    notificationAdapter.updateData(notificationList)
                }
            }
            .addOnFailureListener { exception ->
                Log.w("NotificationsActivity", "Error loading additional care notifications.", exception)
            }
    }
}
