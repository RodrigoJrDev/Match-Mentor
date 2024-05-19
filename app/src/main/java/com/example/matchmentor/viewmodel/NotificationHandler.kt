package com.example.matchmentor.viewmodel

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.matchmentor.R
import com.example.matchmentor.view.HomePageActivity
import kotlin.random.Random

class NotificationHandler(private val context: Context) {
    private val notificationManager = context.getSystemService(NotificationManager::class.java)
    private val notificationChannelID = "notification_channel_id"

    fun showSimpleNotification() {
        // Crie uma intent que abrirá a atividade desejada
        val intent = Intent(context, HomePageActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent = PendingIntent.getActivity(
            context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // Crie a notificação
        val notification = NotificationCompat.Builder(context, notificationChannelID)
            .setContentTitle("Match Encontrado!")
            .setContentText("Você encontrou um novo match!")
            .setSmallIcon(R.drawable.icon) // Substitua pelo seu próprio ícone
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent) // Adicione o pending intent à notificação
            .build()

        // Exiba a notificação
        notificationManager.notify(Random.nextInt(), notification)
    }
}
