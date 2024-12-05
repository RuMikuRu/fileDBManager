import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalDrawer
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.lifecycle.viewmodel.compose.viewModel
import dataBase.DataBaseObject
import kotlinx.coroutines.launch
import model.People
import model.Role
import ui.elements.ModalDrawerNavigation
import ui.elements.TopBarPrimary
import ui.forms.FormAddUser
import ui.forms.FormDeletePeople
import ui.forms.FormEditUser
import ui.forms.FormSearchPeople
import ui.screen.ChartScreen
import ui.screen.DataBaseViewScreen
import viewModel.ChartScreenViewModel
import viewModel.MainScreenViewModel


@Composable
fun App() {
    var isOpenAddUserForm by remember { mutableStateOf(false) }
    var isOpenDeleteUserForm by remember { mutableStateOf(false) }
    var isOpenSearchUserForm by remember { mutableStateOf(false) }
    var isOpenEditUserForm by remember { mutableStateOf(false) }

    val scaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    val db = DataBaseObject.addFilePath("database.json").addKeyField("id").addSerializer(People.serializer())
        .createDataBase<People>()
    val viewModel = viewModel { MainScreenViewModel(db) }
    val chartViewModel = viewModel { ChartScreenViewModel(db) }
    val stateChartsForm by chartViewModel.chartsTestIsOpen.collectAsState()
    val userList by viewModel.userList.collectAsState()
    val time by chartViewModel.time.collectAsState()
    MaterialTheme {
        ModalDrawer(
            drawerElevation = 10.dp,
            gesturesEnabled = true,
            drawerState = scaffoldState.drawerState,
            drawerContent = {
                ModalDrawerNavigation(
                    vm = viewModel,
                    vmCharts = chartViewModel
                )
            },
            content = {
                Scaffold(
                    scaffoldState = scaffoldState,
                    drawerElevation = 200.dp,
                    topBar = {
                        TopBarPrimary(
                            drawerAction = {
                                scope.launch { scaffoldState.drawerState.open() }
                            },
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
                            },
                            updateActionOnClick = {
                                viewModel.updateTable()
                            }
                        )
                    },
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
                    if (stateChartsForm) {
                        ChartScreen(
                            vm = chartViewModel,
                            onDismiss = { chartViewModel.setChartsIsOpen(false) })
                    }
                }
            })
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}