package com.example.scheduletrackervyatsu.domain

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.scheduletrackervyatsu.MainActivity
import com.example.scheduletrackervyatsu.data.ScheduleTrackerDatabase
import com.example.scheduletrackervyatsu.data.ScheduleTrackerRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.net.UnknownHostException

/**
 * Получатель сигнала по таймеру.
 */
class DailyReceiver : BroadcastReceiver() {

    /**
     * Репозиторий для получения данных.
     */

    private lateinit var repository: ScheduleTrackerRepository

    /**
     * Вызывается при получении сигнала.
     */
    override fun onReceive(context: Context?, intent: Intent?) {
        if (context == null) {
            return
        }

        repository = ScheduleTrackerRepository(
            ScheduleTrackerDatabase.getDatabase(context).getScheduleTrackerDao())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val worker = DailyWorker(repository)

                val res = worker.doDailyWork()

                if (res) {
                    sendNotification(
                        context,
                        "Изменения в расписании",
                        "В расписании произошли изменения"
                    )
                }
            }
            catch (ex: UnknownHostException) {
                repository.insertLog("Ошибка подключения к сайту ВятГУ. Подробнее: " + ex.toString())
            }
            catch (ex: Exception) {
                repository.insertLog(ex.toString())
            }
        }
    }

    /**
     * Отправить уведомление.
     */
    fun sendNotification(context: Context?, taskName: String, parsingResult: String) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "parsing_notification_channel"
        val channel = NotificationChannel(channelId,
            "Parsing Notifications",
            NotificationManager.IMPORTANCE_DEFAULT)
        notificationManager.createNotificationChannel(channel)

        val notificationId = taskName.hashCode()

        val notificationBuilder = NotificationCompat.Builder(context!!, channelId).apply {
            setSmallIcon(android.R.drawable.ic_dialog_info)
            setContentTitle(taskName)
            setContentText(parsingResult)
            priority = NotificationCompat.PRIORITY_DEFAULT
            setContentIntent(
                PendingIntent.getActivity(
                    context,
                    0,
                    Intent(context, MainActivity::class.java),
                        PendingIntent.FLAG_IMMUTABLE
                )
            )
            setAutoCancel(true)

        }

        notificationManager.notify(notificationId, notificationBuilder.build())
    }
}

