package com.example.scheduletrackervyatsu

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationCompat
import com.example.compose.ScheduleTrackerTheme
import com.example.scheduletrackervyatsu.ui.components.MyApp
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val PARSING_REQUEST_CODE = 1
    private val MORNING_PARSING_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleTrackerTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }

        scheduleParsingTask(9 , 30, "Morning Parsing")
        //scheduleParsingTask(22, 15, "Evening Parsing")
    }

    private fun scheduleParsingTask(hour: Int, minute: Int, taskName: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(this, ParsingReceiver::class.java)

        val pendingIntent = PendingIntent.getBroadcast(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


        //alarmManager.setRepeating(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            AlarmManager.INTERVAL_DAY,
//            pendingIntent)


        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60,
            pendingIntent
        )
    }
}

class ParsingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val parsingResult = mockParsing()

        context?.let {
            sendNotification(it, "Пока тест", parsingResult[0])
        }

    }

    private fun mockParsing(): List<String> {
        return listOf("Mock parsing result")
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



