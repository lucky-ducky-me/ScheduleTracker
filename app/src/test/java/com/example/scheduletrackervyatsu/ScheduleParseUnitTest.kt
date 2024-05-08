package com.example.scheduletrackervyatsu

import com.example.scheduletrackervyatsu.data.dao.ScheduleReceiver
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ScheduleParseUnitTest {
    @Test
    fun parsingHtmlLinkText_isCorrect() {
        var scheduleReceiver = ScheduleReceiver()

        var dates = scheduleReceiver.parseHtmlLinkText("c 06 05 2024 по 19 05 2024")

        assertEquals(dates[0], "06-05-2024")
        assertEquals(dates[1], "19-05-2024")
    }

    @Test(expected = IllegalArgumentException::class)
    fun parsingHtmlLinkText_invalidData() {
        var scheduleReceiver = ScheduleReceiver()

        scheduleReceiver.parseHtmlLinkText("c 06 052024 по 19 05 2024")
    }

    @Test
    fun testingDate() {
        var currentYear = LocalDate.now().year

        var currentDay = "понедельник 06.05.24 "
            .trim().split(" ")[1].trim()
            .split(".").reversed().mapIndexed{
                    index, s ->  when(index) {
                0 -> currentYear
                else -> s
            }
            }.joinToString("-")

        var test = 1;
    }
}