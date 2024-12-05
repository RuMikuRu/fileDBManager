package viewModel

import coreDB.DataBase
import io.github.serpro69.kfaker.faker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.isActive
import model.People
import model.Role
import ui.core.viewModel.BaseViewModelCoroutines
import kotlin.random.Random
import kotlin.system.measureTimeMillis

class ChartScreenViewModel(private val db: DataBase<People>) : BaseViewModelCoroutines() {
    private val _chartsTestIsOpen = MutableStateFlow(false)
    val chartsTestIsOpen = _chartsTestIsOpen.asStateFlow()

    private val _time = MutableStateFlow(listOf<Long>())
    val time = _time.asStateFlow()

    private val _timeSize = MutableStateFlow(listOf(""))
    val timeSize = _timeSize.asStateFlow()

    private val _quantityRecord = MutableStateFlow(0)
    val quantityRecord = _quantityRecord.asStateFlow()

    private val _quantitySize = MutableStateFlow<Int>(50)
    val quantitySize = _quantitySize.asStateFlow()

    private val _uiState = MutableStateFlow(true)
    val uiState = _uiState.asStateFlow()

    fun testQuantityRecordTest() {
        val faker = faker { }
        val timeList = mutableListOf<Long>() // Хранение времени для каждой итерации
        val timeSizeList = mutableListOf<String>()
        val random = Random(System.currentTimeMillis()) // Общий Random
        _uiState.update{ false }
        doWork(
            doOnAsyncBlock = {
                val recordsToInsert = _quantitySize.value

                for (i in 1..recordsToInsert) {
                    val elapsedMs = measureTimeMillis {
                        for (j in 1..recordsToInsert) {
                            db.addRecord(
                                People(
                                    id = random.nextLong(0, Long.MAX_VALUE),
                                    name = faker.name.name(),
                                    age = random.nextInt(18, 60),
                                    isActive = random.nextBoolean(),
                                    role = Role.USER
                                )
                            )
                        }
                    }
                    timeList.add(elapsedMs) // Сохраняем время выполнения для данной итерации
                    timeSizeList.add(timeList.size.toString())
                }
                _time.update { timeList }
                _timeSize.update { timeSizeList }
                _uiState.update{ true }
                // Обновляем время выполнения и количество записей в StateFlow
                _quantityRecord.update { recordsToInsert }
            }
        )
    }

    fun setChartsIsOpen(value: Boolean) {
        this._chartsTestIsOpen.update { value }
    }
}