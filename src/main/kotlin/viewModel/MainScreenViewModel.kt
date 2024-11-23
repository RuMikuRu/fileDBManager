package viewModel

import coreDB.DataBase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import model.People
import ui.core.viewModel.BaseViewModelCoroutines
import util.toBoolean
import util.toRole

class MainScreenViewModel(private val db: DataBase<People>) : BaseViewModelCoroutines() {
    private val _id = MutableStateFlow("")
    private val _name = MutableStateFlow("")
    private val _age = MutableStateFlow("")
    private val _isActive = MutableStateFlow("")
    private val _role = MutableStateFlow("")

    val id = _id.asStateFlow()
    val name = _name.asStateFlow()
    val age = _age.asStateFlow()
    val isActive = _isActive.asStateFlow()
    val role = _role.asStateFlow()

    private val _userList = MutableStateFlow(listOf<People>())
    val userList = _userList.asStateFlow()

    private val _fieldType = MutableStateFlow("")
    private val _fieldValue = MutableStateFlow("")
    val fieldType = _fieldType.asStateFlow()
    val fieldValue = _fieldValue.asStateFlow()

    private val _searchResult = MutableStateFlow<People?>(null)
    val searchResult = _searchResult.asStateFlow()

    private val _newIdPeople = MutableStateFlow("")
    val newIdPeople = _newIdPeople.asStateFlow()

    init {
        updateTable()
    }

    fun addUser() {
        doWork(doOnAsyncBlock = {
            if (_id.value != "" && _age.value != "") {
                if (db.addRecord(
                        People(
                            id = _id.value.toLong(),
                            name = _name.value,
                            age = _age.value.toInt(),
                            isActive = _isActive.value.toBoolean(),
                            role = _role.value.toRole()
                        )
                    )
                ) {
                    updateTable()
                }
            }
        })
    }

    fun deleteUser() {
        doWork(doOnAsyncBlock = {
            if (_fieldType.value != "") {
                when (_fieldType.value.toLowerCase()) {
                    "id" -> db.deleteRecordByField(field = _fieldType.value, value = _fieldValue.value.toInt())
                    "name" -> db.deleteRecordByField(field = _fieldType.value, value = _fieldValue.value)
                    "age" -> db.deleteRecordByField(field = _fieldType.value, value = _fieldValue.value.toInt())
                    "isactive" -> db.deleteRecordByField(
                        field = _fieldType.value,
                        value = _fieldValue.value.toBoolean()
                    )

                    "role" -> db.deleteRecordByField(field = _fieldType.value, value = _fieldValue.value.toRole())
                }
                updateTable()
            }
        })
    }

    fun searchByField() {
        doWork(doOnAsyncBlock = {
            if (_fieldType.value != "") {
                _searchResult.update {
                    when (_fieldType.value.toLowerCase()) {
                        "id" -> db.searchByField(field = _fieldType.value, value = _fieldValue.value.toInt())
                        "name" -> db.searchByField(field = _fieldType.value, value = _fieldValue.value)
                        "age" -> db.searchByField(field = _fieldType.value, value = _fieldValue.value.toInt())
                        "isactive" -> db.searchByField(
                            field = _fieldType.value,
                            value = _fieldValue.value.toBoolean()
                        )

                        "role" -> db.searchByField(field = _fieldType.value, value = _fieldValue.value.toRole())
                        else -> {
                            throw Exception("User not found")
                        }
                    }
                }
            }
        })
    }

    fun editPeople() {
        doWork(doOnAsyncBlock = {
            if (_newIdPeople.value != "") {
                db.editRecord(
                    _newIdPeople.value.toInt(),
                    People(
                        id = _id.value.toLong(),
                        name = _name.value,
                        age = _age.value.toInt(),
                        isActive = _isActive.value.toBoolean(),
                        role = _role.value.toRole()
                    )
                )
                updateTable()
            }
        })
    }

    fun setPeopleId(id: String) {
        this._id.update { id }
    }

    fun setPeopleName(name: String) {
        this._name.update {
            name
        }
    }

    fun setPeopleAge(age: String) {
        this._age.update { age }
    }

    fun setPeopleIsActive(isActive: String) {
        this._isActive.update {
            isActive
        }
    }

    fun setPeopleRole(role: String) {
        this._role.update {
            role
        }
    }

    fun setFieldType(type: String) {
        this._fieldType.update { type }
    }

    fun setFieldValue(value: String) {
        this._fieldValue.update {
            value
        }
    }

    fun setNewPeopleId(id: String) {
        this._newIdPeople.update {
            id
        }
    }


    private fun updateTable() {
        _userList.update {
            db.getAllRecords()
        }
    }
}