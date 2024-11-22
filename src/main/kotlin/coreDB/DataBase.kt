package coreDB

interface DataBase<T : Any> {
    fun createDatabase()
    fun getAllRecords(): List<T>
    fun addRecord(record: T): Boolean
    fun deleteRecordByField(field: String, value: Any?)
    fun searchByField(field: String, value: Any?): T?
    fun editRecord(keyValue: Any, updateRecord: T): Boolean
    fun createBackup(backupPath: String)
    fun restoreFromBackup(backupPath: String)
}