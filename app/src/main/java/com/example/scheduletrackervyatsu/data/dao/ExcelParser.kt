package com.example.scheduletrackervyatsu.data.dao

import com.example.scheduletrackervyatsu.data.parsing_models.LessonParsingModel
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Workbook
import java.time.LocalDate
import java.time.LocalTime

/**
 * Парсер эксель файлов.
 */
class ExcelParser {

    /**
     * Парсинг эксель файла.
     * @param excelWorkbook эксель файл.
     * @param teachers список преподавателей.
     * @return Словарь вида: ключ - ФИО преподавателя, значение - список занятий.
     */
    fun parseExcelFile(
        excelWorkbook: Workbook,
        teachers: List<String>
    ): HashMap<String, List<LessonParsingModel>> {
        val sheet = excelWorkbook.getSheetAt(0)

        val rowCount = sheet.lastRowNum

        val headerRowNumber = 1
        val headerRow = sheet.getRow(headerRowNumber)

        val teachersColumns = parseHeaderRow(headerRow, teachers)

        if (teachersColumns.isEmpty()) {
            return hashMapOf()
        }

        val result = hashMapOf<String, List<LessonParsingModel>>()

        var currentDay = ""

        var currentDayName = ""

        var cntDay = 0

        for (i in 2..<rowCount) {
            val row = sheet.getRow(i)

            val firstCellVal = row.getCell(0)

            val currentYear = LocalDate.now().year

            currentDay = when (firstCellVal.cellType) {
                CellType.BLANK ->
                    currentDay
                else -> {
                    firstCellVal.stringCellValue.trim().split(" ")[1].trim()
                        .split(".").reversed().mapIndexed{
                                index, s ->  when(index) {
                                    0 -> currentYear
                                    else -> s
                                }
                        }.joinToString("-")
                }
            }

            currentDayName = when (firstCellVal.cellType) {
                CellType.BLANK ->
                    currentDayName
                else -> {
                    firstCellVal.stringCellValue.trim().split(" ")[0].trim()
                }
            }

            val time = row.getCell(1).stringCellValue.trim().split("-")[0].trim()

            for (j in teachersColumns) {
                val cellValue = when (row.getCell(j.second).cellType) {
                    CellType.STRING ->
                         if (row.getCell(j.second).stringCellValue.isNotEmpty())
                            row.getCell(j.second).stringCellValue.trim()
                         else
                             ""
                    else -> {
                        ""
                    }
                }

                //if (cellValue == "") {
                //    continue
                //}

                if (currentDayName == "воскресенье") {
                    cntDay--
                }

                val lessonParsingModel = LessonParsingModel(
                    name = cellValue
                    , date = LocalDate.parse(currentDay)
                    , time = LocalTime.parse(time)
                    , week = cntDay / 6 != 0
                    , dayOfWeek = currentDayName
                    , department = "")

                if (!result.containsKey(j.first)) {
                    result[j.first] = emptyList()
                }

                result[j.first] = result[j.first]!!.plus(listOf(lessonParsingModel))
            }

            if (firstCellVal.cellType != CellType.BLANK) {
                cntDay++
            }
        }

        return result
    }

    /**
     * Парсинг шапки таблицы с расписанием.
     * @param headerRow шапка таблицы.
     * @return список ФИО преподавателей.
     */
    private fun parseHeaderRow(headerRow: Row, teachers: List<String>): List<Pair<String, Int>> {
        val teachersNames = mutableListOf<Pair<String, Int>>()

        val teachersColumnStartIndex = 2;

        for (i in teachersColumnStartIndex..<headerRow.lastCellNum) {
            val cellValue = headerRow.getCell(i).toString().trim()

            if (teachers.contains(cellValue)) {
                teachersNames.add(Pair(cellValue, i))
            }
        }

        return teachersNames
    }
}