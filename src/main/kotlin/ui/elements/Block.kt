package ui.elements

import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
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
fun TopBarPrimary(buttonTitle: String, actionOnClick: () -> Unit) {
    TopAppBar(
        backgroundColor = Color(0xFF845460)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(text = "Просмотрщик базы данных 1.0", color = Color.White)
            Button(
                onClick = { actionOnClick() },
                colors = ButtonDefaults.buttonColors(
                    backgroundColor = Color(0xFFEAD3CB)
                )
            ) {
                Text(text = buttonTitle)
            }
        }
    }
}