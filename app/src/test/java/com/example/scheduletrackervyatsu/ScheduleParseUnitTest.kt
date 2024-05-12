package com.example.scheduletrackervyatsu

import com.example.scheduletrackervyatsu.data.dao.VyatsuParser
import org.junit.Test

import org.junit.Assert.*
import java.time.LocalDate

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ScheduleParseUnitTest {
    @Test
    fun parsingHtmlLinkText_isCorrect() {
        var vyatsuParser = VyatsuParser()

        var dates = vyatsuParser.parseHtmlLinkText("c 06 05 2024 по 19 05 2024")

        assertEquals(dates[0], "06-05-2024")
        assertEquals(dates[1], "19-05-2024")
    }

    @Test(expected = IllegalArgumentException::class)
    fun parsingHtmlLinkText_invalidData() {
        var vyatsuParser = VyatsuParser()

        vyatsuParser.parseHtmlLinkText("c 06 052024 по 19 05 2024")
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