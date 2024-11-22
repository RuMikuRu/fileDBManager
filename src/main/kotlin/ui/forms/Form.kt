package ui.forms

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import ui.elements.Body

@Composable
fun FormAddUser(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    idValue: String = "",
    nameValue: String = "",
    ageValue: String = "",
    isActiveValue: String = "",
    roleValue: String = ""
) {
    var idValueRemember by remember { mutableStateOf(idValue) }
    var nameValueRemember by remember { mutableStateOf(nameValue) }
    var ageValueRemember by remember { mutableStateOf(ageValue) }
    var isActiveValueRemember by remember { mutableStateOf(isActiveValue) }
    var roleValueRemember by remember { mutableStateOf(roleValue) }
    Dialog(
        onDismissRequest = {
            onDismiss()
        },
    ) {
        Card(modifier = Modifier.fillMaxSize().padding(20.dp)) {
            Body(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Id", modifier = Modifier.weight(.2f))
                        TextField(value = idValueRemember, onValueChange = { it -> idValueRemember = it })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Name", modifier = Modifier.weight(.2f))
                        TextField(value = nameValueRemember, onValueChange = { it -> nameValueRemember = it })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "Age", modifier = Modifier.weight(.2f))
                        TextField(value = ageValueRemember, onValueChange = { it -> ageValueRemember = it })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "IsActive", modifier = Modifier.weight(.2f))
                        TextField(value = isActiveValueRemember, onValueChange = { it -> isActiveValueRemember = it })
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "role", modifier = Modifier.weight(.2f))
                        TextField(value = roleValueRemember, onValueChange = { it -> roleValueRemember = it })
                    }
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