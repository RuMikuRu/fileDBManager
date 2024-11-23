package ui.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ui.elements.Body
import ui.elements.FieldWithTitle
import ui.elements.TableScreen
import viewModel.MainScreenViewModel

@Composable
fun FormAddUser(
    vm: MainScreenViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val idValueRemember by vm.id.collectAsState()
    val nameValueRemember by vm.name.collectAsState()
    val ageValueRemember by vm.age.collectAsState()
    val isActiveValueRemember by vm.isActive.collectAsState()
    val roleValueRemember by vm.role.collectAsState()
    Dialog(
        onDismissRequest = {
            with(vm) {
                setPeopleId("")
                setPeopleName("")
                setPeopleAge("")
                setPeopleIsActive("")
                setPeopleRole("")
            }
            onDismiss()
        },
    ) {
        Card(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Body(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FieldWithTitle(title = "Id", value = idValueRemember, onValueChange = { it -> vm.setPeopleId(it) })
                    FieldWithTitle(
                        title = "Name",
                        value = nameValueRemember,
                        onValueChange = { it -> vm.setPeopleName(it) })
                    FieldWithTitle(
                        title = "Age",
                        value = ageValueRemember,
                        onValueChange = { it -> vm.setPeopleAge(it) })
                    FieldWithTitle(
                        title = "IsActive",
                        value = isActiveValueRemember,
                        onValueChange = { it -> vm.setPeopleIsActive(it) })
                    FieldWithTitle(
                        title = "Role",
                        value = roleValueRemember,
                        onValueChange = { it -> vm.setPeopleRole(it) })
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = "Отменить", modifier = Modifier.clickable { onDismiss() })
                            Text(text = "Добавить", modifier = Modifier.clickable { onConfirm() })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun FormDeletePeople(
    vm: MainScreenViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val fieldType by vm.fieldType.collectAsState()
    val fieldValue by vm.fieldValue.collectAsState()

    Dialog(onDismissRequest = {
        vm.setFieldType("")
        vm.setFieldValue("")
        onDismiss()
    }) {
        Card(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Body(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Удаление по полю")
                    FieldWithTitle(title = "Поле", value = fieldType, onValueChange = { it -> vm.setFieldType(it) })
                    FieldWithTitle(
                        title = "Значение",
                        value = fieldValue,
                        onValueChange = { it -> vm.setFieldValue(it) })
                }
            }
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Отменить", modifier = Modifier.clickable { onDismiss() })
                        Text(text = "Удалить", modifier = Modifier.clickable { onConfirm() })
                    }
                }
            }
        }
    }
}

@Composable
fun FormSearchPeople(
    vm: MainScreenViewModel,
    onDismiss: () -> Unit,
) {
    val fieldType by vm.fieldType.collectAsState()
    val fieldValue by vm.fieldValue.collectAsState()
    val searchResult by vm.searchResult.collectAsState()

    Dialog(onDismissRequest = {
        vm.setFieldType("")
        vm.setFieldValue("")
        onDismiss()
    }) {
        Card(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Body(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(text = "Поиск пользователя по полю")
                    FieldWithTitle(title = "Поле", value = fieldType, onValueChange = { it -> vm.setFieldType(it) })
                    FieldWithTitle(
                        title = "Значение",
                        value = fieldValue,
                        onValueChange = { it -> vm.setFieldValue(it) })
                }
                TableScreen(if (searchResult == null) listOf() else listOf(searchResult!!))
            }

            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text(text = "Отменить", modifier = Modifier.clickable { onDismiss() })
                        Text(text = "Найти", modifier = Modifier.clickable { vm.searchByField() })
                    }
                }
            }
        }
    }
}

@Composable
fun FormEditUser(
    vm: MainScreenViewModel,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) {
    val idValueRemember by vm.id.collectAsState()

    val newIdPeople by vm.newIdPeople.collectAsState()
    val nameValueRemember by vm.name.collectAsState()
    val ageValueRemember by vm.age.collectAsState()
    val isActiveValueRemember by vm.isActive.collectAsState()
    val roleValueRemember by vm.role.collectAsState()
    Dialog(
        onDismissRequest = {
            with(vm) {
                setPeopleId("")
                setPeopleName("")
                setPeopleAge("")
                setPeopleIsActive("")
                setPeopleRole("")
            }
            onDismiss()
        },
    ) {
        Card(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Body(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    FieldWithTitle(
                        title = "Id пользователя",
                        value = newIdPeople,
                        onValueChange = { it -> vm.setNewPeopleId(it) })
                    FieldWithTitle(
                        title = "Id",
                        value = idValueRemember,
                        onValueChange = { it -> vm.setPeopleId(it) })
                    FieldWithTitle(
                        title = "Name",
                        value = nameValueRemember,
                        onValueChange = { it -> vm.setPeopleName(it) })
                    FieldWithTitle(
                        title = "Age",
                        value = ageValueRemember,
                        onValueChange = { it -> vm.setPeopleAge(it) })
                    FieldWithTitle(
                        title = "IsActive",
                        value = isActiveValueRemember,
                        onValueChange = { it -> vm.setPeopleIsActive(it) })
                    FieldWithTitle(
                        title = "Role",
                        value = roleValueRemember,
                        onValueChange = { it -> vm.setPeopleRole(it) })
                }
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomEnd) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            Text(text = "Отменить", modifier = Modifier.clickable { onDismiss() })
                            Text(text = "Изменить", modifier = Modifier.clickable { onConfirm() })
                        }
                    }
                }
            }
        }
    }
}


