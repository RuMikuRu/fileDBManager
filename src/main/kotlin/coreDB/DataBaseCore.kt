package coreDBimport

import coreDB.DataBase
import kotlinx.serialization.KSerializer

import kotlinx.serialization.json.*
import java.io.*

class DataBaseCore<T : Any>(
    private val filePath: String,
    private val keyField: String,
    private val serializer: KSerializer<T>
):DataBase<T> {
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
            val record = Json.decodeFromString(serializer, line)
            records.add(record)
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

        if (searchByField(keyField, keyValue) != null) {
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
    override fun searchByField(field: String, value: Any?): T? {
        var result: T? = null
        if (field != keyField) {
            // Обычный линейный поиск для неключевых полей
            File(filePath).forEachLine { line ->
                val record = Json.decodeFromString(serializer, line)
                if (record.getFieldValue(field) == value) {
                    result = record
                }
            }
            return result
        }

        // Быстрый поиск по индексу
        val offset = findOffsetInIndex(value.toString()) ?: return null
        RandomAccessFile(filePath, "r").use { raf ->
            raf.seek(offset)
            val line = raf.readLine()
            return Json.decodeFromString(serializer, line)
        }
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
        File(filePath).copyTo(File(backupPath), overwrite = true)
        println("Backup created successfully.")
    }

    // Восстановление из резервной копии
    override fun restoreFromBackup(backupPath: String) {
        File(backupPath).copyTo(File(filePath), overwrite = true)
        rebuildIndex()
        println("Database restored from backup.")
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
        indexFile.writeText("") // Очищаем старый индекс
        var offset = 0L
        File(filePath).forEachLine { line ->
            val record = Json.decodeFromString(serializer, line)
            val keyValue = record.getFieldValue(keyField)?.toString()
            if (keyValue != null) {
                indexFile.appendText("$keyValue:$offset\n")
            }
            offset += line.toByteArray().size + 1 // Учитываем символ новой строки
        }
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
