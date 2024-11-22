package viewModel

import androidx.compose.ui.text.toLowerCase
import androidx.lifecycle.viewModelScope
import coreDB.DataBase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import model.People
import model.Role
import ui.core.viewModel.BaseViewModelCoroutines
import util.toBoolean
import util.toRole

class MainScreenViewModel(private val db: DataBase<People>) : BaseViewModelCoroutines() {
    private val _userList = MutableStateFlow(listOf<People>())
    val userList = _userList.asStateFlow()


    fun addUser(
        id: String,
        name: String,
        age: String,
        isActive: String,
        role: String
    ) {
        doWork(doOnAsyncBlock = {
            if (id != "" && age != "" && (isActive.toLowerCase() == "true" || isActive.toLowerCase() == "false")) {
                if (db.addRecord(
                        People(
                            id = id.toLong(),
                            name = name,
                            age = age.toInt(),
                            isActive = isActive.toBoolean(),
                            role = role.toRole()
                        )
                    )
                ) {
                    updateTable()
                }
            }
        })
    }

    private fun updateTable() {
        _userList.update {
            db.getAllRecords()
        }
    }
}