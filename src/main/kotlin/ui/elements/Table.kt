package ui.elements

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import model.People

@Composable
private fun RowScope.TableCell(
    text: String,
    weight: Float
) {
    Text(
        text = text,
        Modifier
            .border(1.dp, Color.Black)
            .weight(weight)
            .padding(8.dp)
    )
}

@Composable
fun TableScreen(peopleList:List<People>) {
    val columnWeight = .2f
    LazyColumn(Modifier.fillMaxSize().padding(16.dp)) {
        item {
            Row(Modifier.background(Color.Gray)) {
                TableCell(text = "Id", weight = columnWeight)
                TableCell(text = "Name", weight = columnWeight)
                TableCell(text = "Age", weight = columnWeight)
                TableCell(text = "Is active", weight = columnWeight)
                TableCell(text = "Role", weight = columnWeight)
            }
        }
        items(peopleList) {
            val (id, name, age, isActive, role) = it
            Row(Modifier.fillMaxWidth()) {
                TableCell(text = id.toString(), weight = columnWeight)
                TableCell(text = name, weight = columnWeight)
                TableCell(text = age.toString(), weight = columnWeight)
                TableCell(text = isActive.toString(), weight = columnWeight)
                TableCell(text = role.name, weight = columnWeight)
            }
        }
    }
}