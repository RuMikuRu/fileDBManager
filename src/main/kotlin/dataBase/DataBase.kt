package dataBase

import coreDB.DataBase
import coreDBimport.DataBaseCore
import kotlinx.serialization.KSerializer

object DataBaseObject {
    private var filePath: String? = null
    private var keyField: String? = null
    private var serializer: Any? = null

    fun addFilePath(filePath: String): DataBaseObject {
        DataBaseObject.filePath = filePath
        return DataBaseObject
    }

    fun addKeyField(keyField: String): DataBaseObject {
        DataBaseObject.keyField = keyField
        return DataBaseObject
    }

    fun <T> addSerializer(serializer: KSerializer<T>): DataBaseObject {
        DataBaseObject.serializer = serializer
        return DataBaseObject
    }

    fun <T : Any> createDataBase(): DataBase<T> {
        validate()
        return DataBaseCore<T>(
            filePath!!,
            keyField!!,
            (serializer as? KSerializer<T>) ?: throw TypeCastException("Error cast")
        )
    }

    private fun validate() {
        if (filePath == null) throw NullPointerException("filePath is not Null")
        if (keyField == null) throw NullPointerException("keyField is not Null")
        if (serializer == null) throw NullPointerException("serializer is not Null")
    }
}
