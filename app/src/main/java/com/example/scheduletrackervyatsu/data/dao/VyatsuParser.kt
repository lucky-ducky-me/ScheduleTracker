package com.example.scheduletrackervyatsu.data.dao

import com.example.scheduletrackervyatsu.data.parsing_models.LessonParsingModel
import com.example.scheduletrackervyatsu.data.parsing_models.ScheduleParserData
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jsoup.Jsoup
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import org.jsoup.nodes.Element
import java.time.LocalDateTime

class VyatsuParser(
    val baseUrl: String = "https://www.vyatsu.ru/",
    val scheduleUrl: String = "studentu-1/spravochnaya-informatsiya/teacher.html",
    val teachersUrl: String = "studentu-1/kto-est-kto-v-vyatgu.html"
) {
    /**
     * Получить актуальное расписание.
     * @param teachers Список преподавателей.
     * @param departments Список кафедр.
     * @param startDate Дата начала требуемого расписания.
     * @param endDate Дата конца требуемого расписания.
     * @return Актуальное расписание.
     */
    fun getActualSchedule(
        teachers: List<String>,
        departments: List<String>,
        startDate: LocalDate,
        endDate: LocalDate
    ): ScheduleParserData {

        if (teachers.isEmpty() || departments.isEmpty()) {
            return ScheduleParserData(
                lessons = emptyMap(), LocalDateTime.now()
            )
        }

        val doc = Jsoup.connect(baseUrl + scheduleUrl).get()

        val departmentsNodes = doc.select("div .kafPeriod").filter {
            departments.contains(it.text())
        }

        val excelUrls = parseDepartmentNodes(departmentsNodes, startDate, endDate)

        val lessons: Map<String, List<LessonParsingModel>> = parseExcelFiles(excelUrls, teachers)

        return ScheduleParserData(
            lessons = lessons, LocalDateTime.now()
        )
    }

    fun getDepartments(): List<String> {
        val doc = Jsoup.connect(baseUrl + scheduleUrl).get()

        val departmentsNodes = doc.select("div .kafPeriod")

        return departmentsNodes.map {
            it.text()
        }
    }

    fun getTeachers(): List<String> {
        val doc = Jsoup.connect(baseUrl + teachersUrl).get()

        var teachersItems = doc.select("div .prepod_item")

        var list = mutableListOf<String>()

        teachersItems.forEach {
            var first = it.children().find {
                it.text().contains("ФИО")
            }

            var ffdsfio = first?.text()?.split(" ")

            var fio = ffdsfio?.subList(1, ffdsfio?.size ?: 3)?.joinToString(" ")

            if (fio != null) {
                list.add(fio)
            }
        }

        return list.toList()
    }

    /**
     * Парсинг эксель файлов.
     * @param excelUrls Ссылки на эксель файлы.
     * @param teachers Список преподавателей.
     * @return Словарь вида: ключ - ФИО преподавателя, значение - список занятий.
     */
    internal fun parseExcelFiles(
        excelUrls: List<Pair<String,String>>,
        teachers: List<String>
    ): HashMap<String, List<LessonParsingModel>> {
        val parsedData: HashMap<String, List<LessonParsingModel>> = hashMapOf()

        val excelParser = ExcelParser()

        excelUrls.forEach {
            val depName = it.first
            val url = URL(baseUrl + it.second)

            with(url.openConnection() as HttpURLConnection) {
                requestMethod = "GET"

                val xlWb = WorkbookFactory.create(inputStream)

                val pairs = excelParser.parseExcelFile(xlWb, teachers)

                pairs.forEach { pair ->
                    val (teacher, lessons) = pair

                    if (!parsedData.contains(teacher)) {
                        parsedData[teacher] = emptyList()
                    }

                    for (element in lessons) {
                        element.department = depName
                    }

                    parsedData[teacher] = parsedData[teacher]!!.plus(lessons)
                }
            }
        }

        return parsedData
    }

    /**
     * Парсинг элементов кафедр.
     */
    internal fun parseDepartmentNodes(
        elements: List<Element>,
        startDate: LocalDate,
        endDate: LocalDate): List<Pair<String, String>> {
        var resultList: List<Pair<String, String>> = listOf()
        val formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy")

        elements.forEach {
            val depName = it.text().trim()
            val htmlLinks = it.nextElementSibling().select("a[href$=\".html\"]")

            val excelLinks = htmlLinks.filter { link ->
                val dates = parseHtmlLinkText(link.text())
                var start = LocalDate.parse(dates[0], formatter)
                var end =  LocalDate.parse(dates[1], formatter)

                var cond = end >= startDate && startDate >= start
                        ||  end >= endDate && endDate >= start

                end >= startDate && startDate >= start
                        ||  end >= endDate && endDate >= start
            }

            resultList = resultList.plus(excelLinks.mapNotNull {
                    link -> Pair(depName, link.nextElementSibling().attr("href"))
            })
        }

        return resultList
    }

    /**
     * Парсинг текста html ссылки, где указывается даты расписания.
     */
    internal fun parseHtmlLinkText(text: String): List<String> {
        val newParsed = text.trim().split(" ").filter {
            it.toIntOrNull() != null
        }

        if (newParsed.size != 6) {
            throw IllegalArgumentException("В функцию ${::parseHtmlLinkText.name} параметр $text передан неверно.")
        }

        return listOf(
            newParsed.subList(0, 3).joinToString("-"),
            newParsed.subList(3, 6).joinToString("-")
        )
    }
}