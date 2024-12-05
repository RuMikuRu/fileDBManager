package coreDB

import kotlinx.serialization.KSerializer

interface DataBase<T : Any> {
    fun createDatabase()
    fun getAllRecords(): List<T>
    fun addRecord(record: T): Boolean
    fun deleteRecordByField(field: String, value: Any?)
    fun searchByField(field: String, value: Any?): List<T>
    fun editRecord(keyValue: Any, updateRecord: T): Boolean
    fun createBackup(backupPath: String)
    fun restoreFromBackup(backupPath: String)
    fun importFromExcel(excelFilePath: String): Boolean
    fun exportToExcel(excelFilePath: String): Boolean
}