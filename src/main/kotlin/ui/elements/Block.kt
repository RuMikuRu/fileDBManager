package ui.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun Body(modifier: Modifier, content: @Composable () -> Unit) {
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        content()
    }
}

@Composable
fun TopBarPrimary(
    actionOnClick: () -> Unit,
    deleteActionOnClick: () -> Unit,
    searchActionOnClick: () -> Unit,
    editActionOnClick: () -> Unit
) {
    TopAppBar(
        backgroundColor = Color(0xFF845460)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Просмотрщик базы данных 1.0", color = Color.White)
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
            }
        }
    }
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