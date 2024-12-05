package ui.screen

import androidx.compose.animation.core.EaseInOutCubic
import androidx.compose.runtime.getValue
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.Window
import ir.ehsannarmani.compose_charts.LineChart
import ir.ehsannarmani.compose_charts.models.AnimationMode
import ir.ehsannarmani.compose_charts.models.DividerProperties
import ir.ehsannarmani.compose_charts.models.DrawStyle
import ir.ehsannarmani.compose_charts.models.LabelProperties
import ir.ehsannarmani.compose_charts.models.Line
import ir.ehsannarmani.compose_charts.models.LineProperties
import ui.elements.Body
import viewModel.ChartScreenViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChartScreen(vm: ChartScreenViewModel, quantity: Int = 0, onDismiss: () -> Unit) {
    val time by vm.time.collectAsState()
    val timeSize by vm.timeSize.collectAsState()
    val uiState by vm.uiState.collectAsState()
    Window(onCloseRequest = { onDismiss() }) {
        Scaffold() {
            Body(modifier = Modifier) {
                Button(onClick = { vm.testQuantityRecordTest() }) {
                    Text(text = "Запустить тест")
                }
                if (uiState) {
                    LineChart(
                        modifier = Modifier.fillMaxSize(),
                        data = remember {
                            listOf(
                                Line(
                                    label = "",
                                    values = time.map { it.toDouble() },
                                    color = SolidColor(Color(0xFF23af92)),
                                    firstGradientFillColor = Color(0xFF2BC0A1).copy(alpha = .5f),
                                    secondGradientFillColor = Color.Transparent,
                                    strokeAnimationSpec = tween(2000, easing = EaseInOutCubic),
                                    gradientAnimationDelay = 1000,
                                    drawStyle = DrawStyle.Stroke(width = 2.dp),
                                )
                            )
                        },
                        dividerProperties = DividerProperties(
                            enabled = true,
                            xAxisProperties = LineProperties(

                            )
                        ),
                        labelProperties = LabelProperties(
                            enabled = true,
                            labels = timeSize
                        ),
                        animationMode = AnimationMode.Together(delayBuilder = {
                            it * 500L
                        }),
                    )
                } else {
                    Text(text = "Выполняю тестирование...")
                }
            }
        }
    }
}