package com.example.scheduletrackervyatsu

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import com.example.scheduletrackervyatsu.ui.theme.ScheduleTrackerTheme
import com.example.scheduletrackervyatsu.domain.DailyReceiver
import com.example.scheduletrackervyatsu.ui.components.MyApp
import java.time.LocalDateTime
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleTrackerTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }

        val hour = LocalDateTime.now().hour
        val minute = LocalDateTime.now().minute + 1

        scheduleParsingTask(hour , minute)
    }

    private fun scheduleParsingTask(hour: Int, minute: Int) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(this, DailyReceiver::class.java)

        val checkPendingIntent = (PendingIntent.getBroadcast(
            this, 0,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE) != null)

        if (!checkPendingIntent) {

            val pendingIntent = PendingIntent.getBroadcast(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)


            alarmManager.setInexactRepeating(
                AlarmManager.RTC_WAKEUP,
                calendar.timeInMillis,
                3000 * 60,
                pendingIntent
            )
        }
    }
}



