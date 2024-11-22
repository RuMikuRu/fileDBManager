package ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import model.People
import ui.elements.Body
import ui.elements.TableScreen

@Composable
fun DataBaseViewScreen(peopleList: List<People>) {
    Body(modifier = Modifier) {
        TableScreen(peopleList)
    }
}