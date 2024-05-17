package com.example.scheduletrackervyatsu.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DailyReceiver : BroadcastReceiver() {

    /**
     * Репозиторий для получения данных.
     */

    private lateinit var repository: ScheduleTrackerRepository

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.d("my", "Типо зашли ${context.toString()}")

        if (context == null) {
            return
        }

        repository = ScheduleTrackerRepository(
            ScheduleTrackerDatabase.getDatabase(context).getScheduleTrackerDao())



        CoroutineScope(Dispatchers.IO).launch {
            val worker = DailyWorker(repository)

            worker.doDailyWork()


            sendNotification(context, "Пока тест", "Тестик")
        }
    }

    private fun sendNotification(context: Context?, taskName: String, parsingResult: String) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "parsing_notification_channel"
        val channel = NotificationChannel(channelId, "Parsing Notifications", NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notificationId = taskName.hashCode()
        val notificationBuilder = NotificationCompat.Builder(context!!, channelId).apply {
            setSmallIcon(android.R.drawable.ic_dialog_info)
            setContentTitle(taskName)
            setContentText(parsingResult)
            priority = NotificationCompat.PRIORITY_DEFAULT
        }

        Log.d("my", "Типо уведомляшку отправили")
        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

