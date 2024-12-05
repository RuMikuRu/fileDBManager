package ui.elements

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.darkrockstudios.libraries.mpfilepicker.DirectoryPicker
import com.darkrockstudios.libraries.mpfilepicker.FilePicker
import viewModel.ChartScreenViewModel
import viewModel.MainScreenViewModel

@Composable
fun Body(modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        content()
    }
}

@Composable
fun TopBarPrimary(
    drawerAction: () -> Unit,
    actionOnClick: () -> Unit,
    deleteActionOnClick: () -> Unit,
    searchActionOnClick: () -> Unit,
    editActionOnClick: () -> Unit,
    updateActionOnClick: () -> Unit
) {
    TopAppBar(
        backgroundColor = Color(0xFF845460)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = { drawerAction() }) {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = "drawerActopn",
                        tint = Color(0xFFEAD3CB)
                    )
                }
                Text(text = "Просмотрщик базы данных 1.0", color = Color.White)
            }
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                IconButton(
                    onClick = { actionOnClick() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Добавить пользователя",
                        tint = Color(0xFFEAD3CB)
                    )
                }

                IconButton(
                    onClick = {
                        deleteActionOnClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Удалить пользователя",
                        tint = Color(0xFFEAD3CB)
                    )
                }
                IconButton(
                    onClick = {
                        searchActionOnClick()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Поиск пользователя",
                        tint = Color(0xFFEAD3CB)
                    )
                }
                IconButton(onClick = { editActionOnClick() }) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Редактирование пользователя",
                        tint = Color(0xFFEAD3CB)
                    )
                }
                IconButton(onClick = { updateActionOnClick() }) {
                    Icon(
                        imageVector = Icons.Default.Build,
                        contentDescription = "Редактирование пользователя",
                        tint = Color(0xFFEAD3CB)
                    )
                }
            }
        }
    }
}

@Composable
fun ModalDrawerNavigation(
    vm: MainScreenViewModel,
    vmCharts: ChartScreenViewModel
) {
    val stateCharts by vmCharts.chartsTestIsOpen.collectAsState()
    var showDirectoryPicker by remember { mutableStateOf(false) }
    var showFilePicker by remember { mutableStateOf(false) }
    var state by remember { mutableStateOf(ActionFile.OPEN) }
    Column(modifier = Modifier.width(200.dp).fillMaxHeight(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        DrawerItem(title = "Открыть базу данный") {
            showFilePicker = true
            state = ActionFile.OPEN
        }
        DrawerItem(title = "Создать бэкап") {
            showDirectoryPicker = true
        }
        DrawerItem(title = "Восстановить из бэкапа") {
            showFilePicker = true
            ActionFile.RE_BACKUP
        }
        DrawerItem(title = "Экспорт в Exel") {
            showDirectoryPicker = true
            state = ActionFile.EXPORT_EXEL
        }
        DrawerItem(title = "Стресс тест") {
            vmCharts.setChartsIsOpen(!stateCharts)
        }
    }
    DirectoryPicker(show = showDirectoryPicker) { path ->
        showDirectoryPicker = false
        if(state == ActionFile.EXPORT_EXEL){
            vm.exportToExcel(path ?: "")
        } else {
            vm.setPath(path ?: "")
            vm.createBackup()
        }
    }
    FilePicker(show = showFilePicker, fileExtensions = listOf("json")) { path ->
        when (state) {
            ActionFile.OPEN -> {
                vm.setPath(path?.path ?: "")
                vm.openDataBase()
            }

            ActionFile.RE_BACKUP -> {
                vm.setPath(path?.path ?: "")
                vm.openDataBase()
            }

            ActionFile.EXPORT_EXEL -> TODO()
        }
        showFilePicker = false
    }
}

enum class ActionFile {
    OPEN,
    RE_BACKUP,
    EXPORT_EXEL
}

@Composable
fun FieldWithTitle(
    title: String,
    value: String,
    onValueChange: (String) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(text = title, modifier = Modifier.weight(.2f))
        TextField(value = value, onValueChange = onValueChange)
    }
}

@Composable
private fun DrawerItem(title: String, imageVector: ImageVector? = null, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(role = Role.Button) { onClick() },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (imageVector != null) {
            Icon(imageVector = imageVector, contentDescription = "", tint = Color(0xFFEAD3CB))
        }
        Text(text = title)
    }
}