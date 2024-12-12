package coreDBimport

import coreDB.DataBase
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy

import kotlinx.serialization.json.*
import kotlinx.serialization.serializer
import org.apache.poi.ss.usermodel.CellType
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.*
import kotlin.jvm.java

class DataBaseCore<T : Any>(
    private val filePath: String,
    private val keyField: String,
    private val serializer: KSerializer<T>
) : DataBase<T> {
    private val indexPath = "$filePath.index"

    init {
        // Создаём файлы, если их нет
        File(filePath).apply { if (!exists()) createNewFile() }
        File(indexPath).apply { if (!exists()) createNewFile() }
    }

    // Создание новой базы данных (очистка файлов)
    override fun createDatabase() {
        File(filePath).writeText("")
        File(indexPath).writeText("")
        println("Database created successfully.")
    }

    override fun getAllRecords(): List<T> {
        val records = mutableListOf<T>()
        File(filePath).forEachLine { line ->
            if (line.isNotBlank()) {
                try {
                    val record = Json.decodeFromString(serializer, line)
                    records.add(record)
                } catch (e: Exception) {
                    println("Skipping malformed line: $line. Error: ${e.message}")
                }
            }
        }
        return records
    }

    // Добавление записи
    override fun addRecord(record: T): Boolean {
        val keyValue = record.getFieldValue(keyField)
        if (keyValue == null) {
            println("Key value is null. Record not added.")
            return false
        }

        if (searchByField(keyField, keyValue).isNotEmpty()) {
            println("Duplicate key found. Record not added.")
            return false
        }

        // Добавляем запись в основной файл
        val file = File(filePath)
        val offset = file.length() // Смещение записи
        file.appendText(Json.encodeToString(serializer, record) + "\n")

        // Обновляем индекс
        updateIndex(keyValue.toString(), offset)
        println("Record added successfully.")
        return true
    }

    // Удаление записи по значению поля
    override fun deleteRecordByField(field: String, value: Any?) {
        val tempFile = File("$filePath.tmp")
        val file = File(filePath)
        var deletedCount = 0

        // Открываем временный файл для записи
        tempFile.bufferedWriter().use { writer ->
            file.forEachLine { line ->
                if (line.isNotBlank()) { // Игнорируем пустые строки
                    val record = Json.decodeFromString(serializer, line)
                    val fieldValue = record.getFieldValue(field)
                    if (fieldValue?.toString() == value?.toString()) {
                        deletedCount++
                        println("Deleting record with $field=$value")
                    } else {
                        writer.write(line)
                        writer.newLine()
                    }
                }
            }
        }

        // Переносим временный файл на место оригинального
        if (file.delete() && tempFile.renameTo(file)) {
            rebuildIndex() // Перестраиваем индекс
            println("Deleted $deletedCount record(s).")
        } else {
            println("Failed to replace the original file. Deletion aborted.")
        }
    }

    // Поиск записи по ключевому полю
    override fun searchByField(field: String, value: Any?): List<T> {
        val results = mutableListOf<T>()

        if (field != keyField) {
            // Обычный линейный поиск для неключевых полей
            File(filePath).forEachLine { line ->
                if (line.isNotBlank()) { // Проверяем, что строка не пустая
                    try {
                        val record = Json.decodeFromString(serializer, line)
                        if (record.getFieldValue(field) == value) {
                            results.add(record)
                        }
                    } catch (e: Exception) {
                        println("Error decoding line: $line, ${e.message}")
                    }
                }
            }
            return results
        }

        // Быстрый поиск по индексу для ключевого поля
        val offset = findOffsetInIndex(value.toString()) ?: return results
        RandomAccessFile(filePath, "r").use { raf ->
            raf.seek(offset)
            while (true) {
                val line = raf.readLine() ?: break
                if (line.isNotBlank()) { // Проверяем, что строка не пустая
                    val record = Json.decodeFromString(serializer, line)
                    val keyFieldValue = record.getFieldValue(keyField)
                    if (keyFieldValue?.toString() == value?.toString()) {
                        results.add(record)
                    } else {
                        break // Останавливаем поиск, если ключ больше не совпадает
                    }
                }
            }
        }

        return results
    }

    override fun editRecord(keyValue: Any, updateRecord: T): Boolean {
        val tempFile = File("$filePath.tmp")
        val file = File(filePath)
        var updated = false

        tempFile.bufferedWriter().use { writer ->
            file.forEachLine { line ->
                if (line.isNotBlank()) { // Игнорируем пустые строки
                    val record = Json.decodeFromString(serializer, line)
                    val fieldValue = record.getFieldValue(keyField)

                    // Приведение к строке для корректного сравнения
                    if (fieldValue?.toString() == keyValue.toString()) {
                        writer.write(Json.encodeToString(serializer, updateRecord))
                        writer.newLine()
                        updated = true
                    } else {
                        writer.write(line)
                        writer.newLine()
                    }
                }
            }
        }

        println("Temp file exists: ${tempFile.exists()}")
        println("Original file exists: ${file.exists()}")

        if (file.delete()) {
            println("Original file deleted.")
            if (tempFile.renameTo(file)) {
                rebuildIndex() // Перестраиваем индекс
                println(if (updated) "Record updated successfully." else "Record not found.")
                return updated
            } else {
                println("Failed to rename temp file to original file.")
                return false
            }
        } else {
            println("Failed to delete the original file.")
            return false
        }
    }


    // Создание резервной копии
    override fun createBackup(backupPath: String) {
        println(backupPath)
        File(filePath).copyTo(File(backupPath + File.separator + "backup.json"), overwrite = true)
        println("Backup created successfully.")
    }

    // Восстановление из резервной копии
    override fun restoreFromBackup(backupPath: String) {
        try {
            File(backupPath).copyTo(File(filePath), overwrite = true)
            println("Backup restored to $filePath. Rebuilding index...")
            rebuildIndex()

            // Проверяем содержимое базы данных и индекса
            val dbContent = File(filePath).readText()
            val indexContent = File(indexPath).readText()
            println("Database content:\n$dbContent")
            println("Index content:\n$indexContent")
        } catch (e: Exception) {
            println("Error restoring from backup: ${e.message}")
            e.printStackTrace()
        }
    }

    override fun importFromExcel(
        excelFilePath: String,
    ): Boolean {
        val file = File(excelFilePath)
        if (!file.exists()) {
            println("Excel file not found at path: $excelFilePath")
            return false
        }

        try {
            FileInputStream(file).use { fis ->
                val workbook = WorkbookFactory.create(fis)
                val sheet = workbook.getSheetAt(0) // Используем первый лист

                // Заголовки колонок
                val headers = sheet.getRow(0)?.map { it.stringCellValue } ?: emptyList()
                if (headers.isEmpty()) {
                    println("No headers found in the Excel sheet.")
                    return false
                }

                // Обрабатываем каждую строку, начиная со второй (первая — заголовок)
                for (row in sheet) {
                    if (row.rowNum == 0) continue // Пропускаем строку заголовков
                    val recordFields = mutableMapOf<String, Any?>()

                    for (cell in row) {
                        val value = when (cell.cellType) {
                            CellType.STRING -> cell.stringCellValue
                            CellType.NUMERIC -> cell.numericCellValue
                            CellType.BOOLEAN -> cell.booleanCellValue
                            else -> null
                        }
                        val columnName = headers.getOrNull(cell.columnIndex)
                        if (columnName != null) {
                            recordFields[columnName] = value
                        }
                    }

                    // Преобразуем Map в JSON-строку и десериализуем в объект типа T
                    val jsonElement = Json.encodeToJsonElement(recordFields)
                    val jsonObject = jsonElement.jsonObject
                    val record = Json.decodeFromJsonElement(serializer, jsonObject)

                    // Добавляем запись в базу
                    addRecord(record)
                }
                workbook.close()
            }
            println("Data imported successfully from Excel.")
            return true
        } catch (e: Exception) {
            println("Error while importing data from Excel: ${e.message}")
            e.printStackTrace()
            return false
        }
    }


    override fun exportToExcel(excelFilePath: String): Boolean {
        try {
            // Создаем книгу и лист Excel
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("Database Records")

            // Получаем записи из базы данных
            val records = getAllRecords()

            // Если записи пусты, сообщаем об этом
            if (records.isEmpty()) {
                println("No records to export.")
                return false
            }

            // Генерация заголовков
            val firstRecord = records.first()
            val headers = firstRecord::class.members
                .filter { it.parameters.size == 1 } // Оставляем только свойства
                .map { it.name }

            // Записываем заголовки в первую строку
            val headerRow = sheet.createRow(0)
            headers.forEachIndexed { index, header ->
                val cell = headerRow.createCell(index)
                cell.setCellValue(header)
            }

            // Записываем данные записей
            records.forEachIndexed { rowIndex, record ->
                val row = sheet.createRow(rowIndex + 1)
                headers.forEachIndexed { colIndex, fieldName ->
                    val fieldValue = record.getFieldValue(fieldName)?.toString() ?: ""
                    val cell = row.createCell(colIndex)
                    cell.setCellValue(fieldValue)
                }
            }

            // Сохраняем Excel-файл
            FileOutputStream(excelFilePath + File.separator + "backap.xls").use { fos ->
                workbook.write(fos)
            }
            workbook.close()

            println("Data exported successfully to $excelFilePath.")
            return true
        } catch (e: Exception) {
            println("Error while exporting data to Excel: ${e.message}")
            return false
        }
    }


    // Получение значения поля объекта
    private fun T.getFieldValue(fieldName: String): Any? {
        return this::class.members
            .firstOrNull { it.name == fieldName }
            ?.let { member ->
                try {
                    member.call(this)
                } catch (e: Exception) {
                    println("Error getting field value: ${e.message}")
                    null
                }
            }
    }

    // Обновление индекса
    private fun updateIndex(key: String, offset: Long) {
        File(indexPath).appendText("$key:$offset\n")
    }

    // Построение индекса с нуля
    private fun rebuildIndex() {
        val indexFile = File(indexPath)
        val dbFile = File(filePath)

        // Проверка наличия файла базы данных
        if (!dbFile.exists() || !dbFile.canRead()) {
            println("Database file $filePath is missing or unreadable.")
            return
        }

        // Очищаем старый индекс
        indexFile.writeText("")
        var offset = 0L

        dbFile.forEachLine { line ->
            if (line.isNotBlank()) { // Игнорируем пустые строки
                try {
                    val record = Json.decodeFromString(serializer, line)
                    val keyValue = record.getFieldValue(keyField)?.toString()

                    if (keyValue != null) {
                        indexFile.appendText("$keyValue:$offset\n")
                    }
                } catch (e: Exception) {
                    println("Error rebuilding index: ${e.message}")
                }
            }
            offset += line.toByteArray().size + 1 // Учитываем символ новой строки
        }

        println("Index rebuilt successfully.")
    }

    // Поиск смещения записи по ключу в индексе
    private fun findOffsetInIndex(key: String): Long? {
        var offsetResult: Long? = null
        File(indexPath).forEachLine { line ->
            val (indexedKey, offset) = line.split(":")
            if (indexedKey == key) {
                offsetResult = offset.toLong()
            }
        }
        return offsetResult
    }
}
