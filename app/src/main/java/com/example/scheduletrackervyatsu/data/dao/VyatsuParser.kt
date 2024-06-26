package com.example.scheduletrackervyatsu.data.dao

import com.example.scheduletrackervyatsu.RUSSIAN_ALPHABET
import com.example.scheduletrackervyatsu.SCHEDULE_URL
import com.example.scheduletrackervyatsu.TEACHER_API_URL
import com.example.scheduletrackervyatsu.VYATSU_URL
import com.example.scheduletrackervyatsu.data.parsing_models.LessonParsingModel
import com.example.scheduletrackervyatsu.data.parsing_models.ScheduleParserData
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.HttpURLConnection
import java.net.URL
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VyatsuParser(
    val baseUrl: String = VYATSU_URL,
    val scheduleUrl: String = SCHEDULE_URL,
    val teachersUrl: String = TEACHER_API_URL
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

    /**
     * Получить список кафедр.
     */
    fun getDepartments(): List<String> {
        val doc = Jsoup.connect(baseUrl + scheduleUrl).get()

        val departmentsNodes = doc.select("div .kafPeriod")

        return departmentsNodes.map {
            it.text()
        }
    }

    /**
     * Получить список преподавателей.
     */
    fun getTeachers(): List<Pair<String,String?>> {
        val list = mutableListOf<Pair<String,String?>>()

        for (letter in RUSSIAN_ALPHABET) {
            val doc = Jsoup.connect(baseUrl + teachersUrl)
                .data("fio", letter.toString())
                .post()

            val teachersItems = doc.select("div .prepod_item")

            teachersItems.forEach {
                val fioNode = it.children().find {
                    it.text().contains("ФИО")
                }

                var departmentNode = it.children().find {
                    it.text().contains("Кафедра")
                }

                val fioSplit = fioNode?.text()?.split(" ")

                val fio = fioSplit?.subList(1, fioSplit?.size ?: 3)?.joinToString(" ")

                if (fio != null && list.find { elem -> elem.first == fio } == null) {
                    list.add(Pair(fio, departmentNode?.text()?.trim()))
                }
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

                end >= startDate && startDate >= start
                        || end >= endDate && endDate >= start
                        || end >= endDate && start <= startDate
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