import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import dataBase.DataBaseObject
import model.People
import model.Role
import ui.elements.TopBarPrimary
import ui.forms.FormAddUser
import ui.forms.FormDeletePeople
import ui.forms.FormEditUser
import ui.forms.FormSearchPeople
import ui.screen.DataBaseViewScreen
import viewModel.MainScreenViewModel

val mockUserList = listOf(
    People(
        id = 1L,
        name = "Iliya",
        age = 10,
        isActive = true,
        role = Role.ADMIN
    ),
    People(
        id = 1L,
        name = "Iliya",
        age = 10,
        isActive = true,
        role = Role.ADMIN
    )
)

@Composable
@Preview
fun App() {
    var isOpenAddUserForm by remember { mutableStateOf(false) }
    var isOpenDeleteUserForm by remember { mutableStateOf(false) }
    var isOpenSearchUserForm by remember { mutableStateOf(false) }
    var isOpenEditUserForm by remember { mutableStateOf(false) }
    val db = DataBaseObject.addFilePath("database.json").addKeyField("id").addSerializer(People.serializer())
        .createDataBase<People>()
    val viewModel = viewModel { MainScreenViewModel(db) }
    val userList by viewModel.userList.collectAsState()
    MaterialTheme {
        Scaffold(
            topBar = {
                TopBarPrimary(
                    actionOnClick = {
                        isOpenAddUserForm = true
                    },
                    deleteActionOnClick = {
                        isOpenDeleteUserForm = true
                    }, searchActionOnClick = {
                        isOpenSearchUserForm = true
                    },
                    editActionOnClick = {
                        isOpenEditUserForm = true
                    }
                )
            }
        ) {
            DataBaseViewScreen(
                peopleList = userList
            )
            if (isOpenAddUserForm) {
                FormAddUser(
                    vm = viewModel,
                    onConfirm = {
                        viewModel.addUser()
                    },
                    onDismiss = { isOpenAddUserForm = false }
                )
            }
            if (isOpenDeleteUserForm) {
                FormDeletePeople(
                    vm = viewModel,
                    onConfirm = { viewModel.deleteUser() },
                    onDismiss = { isOpenDeleteUserForm = false })
            }
            if (isOpenSearchUserForm) {
                FormSearchPeople(
                    vm = viewModel,
                    onDismiss = { isOpenSearchUserForm = false }
                )
            }
            if (isOpenEditUserForm) {
                FormEditUser(
                    vm = viewModel,
                    onConfirm = { viewModel.editPeople() },
                    onDismiss = { isOpenEditUserForm = false }
                )
            }
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}