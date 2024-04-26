package com.example.scheduletrackervyatsu

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.foundation.lazy.LazyColumn
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material.icons.Icons.Filled
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.scheduletrackervyatsu.ui.theme.ScheduleTrackerVyatsuTheme
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.core.app.NotificationCompat
import com.android.volley.toolbox.Volley
import com.android.volley.Request
import com.android.volley.toolbox.StringRequest
import com.example.scheduletrackervyatsu.ui.components.Day
import com.example.scheduletrackervyatsu.ui.components.MyApp
import com.example.scheduletrackervyatsu.ui.components.Section
import java.util.Calendar

class MainActivity : ComponentActivity() {

    private val PARSING_REQUEST_CODE = 1
    private val MORNING_PARSING_REQUEST_CODE = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ScheduleTrackerVyatsuTheme {
                MyApp(modifier = Modifier.fillMaxSize())
            }
        }

        // Schedule parsing task at 6 AM and 8 PM every day
        //scheduleParsingTask(9 , 30, "Morning Parsing")
        //scheduleParsingTask(22, 15, "Evening Parsing")
    }

    private fun getMockData(): MutableList<String> {
        val list = mutableListOf<String>()

        for (i in 0..10) {
            list.add(i.toString() + "FFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF")
        }

        return list
    }

    private fun scheduleParsingTask(hour: Int, minute: Int, taskName: String) {
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            // If the scheduled time has already passed, schedule for the next day
            if (timeInMillis <= System.currentTimeMillis()) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val intent = Intent(this, ParsingReceiver::class.java).apply {
            putExtra("taskName", taskName)
        }

        val pendingIntent = PendingIntent.getBroadcast(this,
            getPendingIntentCode(taskName),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

            /*
        alarmManager.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent)*/


        alarmManager?.setRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            1000 * 60,
            pendingIntent
        )

    }

    private fun getPendingIntentCode(taskName: String): Int {
        return when (taskName) {
            "Morning Parsing" -> MORNING_PARSING_REQUEST_CODE
            else -> PARSING_REQUEST_CODE
        }
    }
}

class ParsingReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        val taskName = intent?.getStringExtra("taskName")

        Log.d("my", "Затригерились")
        if (!taskName.isNullOrEmpty()) {
            Log.d("my", "TaskName не null")
            // Perform parsing task (mocking)
            val parsingResult = mockParsing()
            // Send notification with parsing result
            context?.let {

                sendNotification(it, taskName, parsingResult)
            }
        }
    }

    private fun mockParsing(): String {
        // Mock parsing task, return some result
        return "Mock parsing result"
    }

    private fun sendNotification(context: Context?, taskName: String, parsingResult: String) {
        val notificationManager = context?.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "parsing_notification_channel"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(channelId, "Parsing Notifications", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }

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



