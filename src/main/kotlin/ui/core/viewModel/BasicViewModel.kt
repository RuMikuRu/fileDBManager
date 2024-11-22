package ui.core.viewModel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.CoroutineContext

abstract class BaseViewModelCoroutines() : ViewModel() {
    private var viewModelJob = Job()
    private val viewModelScope = CoroutineScope(Default + viewModelJob)
    private var isActive = true
    private val _progressBarState = MutableStateFlow(false)

    /**
     * Do work in IO
     */
    fun <T> doWork(
        doOnAsyncBlock: suspend CoroutineScope.() -> T,
        error: (throwable: Throwable) -> Unit = { it.printStackTrace() }
    ) {
        try {
            doCoroutineWork(doOnAsyncBlock, viewModelScope, IO)
        } catch (e: Exception) {
            error.invoke(e)
        }
    }

    /**
     * Do work in MainThread
     */
    fun <T> doWorkInMainThread(doAsyncBlock: suspend CoroutineScope.() -> T) {
        doCoroutineWork(doAsyncBlock, viewModelScope, Main)
    }

    /**
     * Do work in IO repeately
     * doRepeateWork(){...}
     * then we need to stop it calling stopRepeatWork()
     */
    fun <T> doRepeatWork(delay: Long, doAsyncBlock: suspend CoroutineScope.() -> T) {
        isActive = true
        viewModelScope.launch {
            while (this@BaseViewModelCoroutines.isActive) {
                withContext(IO) {
                    doAsyncBlock.invoke(this)
                }
                if (this@BaseViewModelCoroutines.isActive) {
                    delay(delay)
                }
            }
        }
    }

    /**
     * Do work in IO Thread with progressBar
     */
    fun <T> doWorkWithProgressBar(doAsyncBlock: suspend CoroutineScope.() -> T) {
        doCoroutinesWorkWithProgressBar(doAsyncBlock, viewModelScope, IO)
    }

    fun stopRepeatWork() {
        isActive = false
    }

    override fun onCleared() {
        super.onCleared()
        isActive = false
        viewModelJob.cancel()
    }

    private inline fun <T> doCoroutineWork(
        crossinline doOnAsyncBlock: suspend CoroutineScope.() -> T,
        coroutineScope: CoroutineScope,
        context: CoroutineContext
    ) {
        coroutineScope.launch {
            withContext(context) {
                try {
                    doOnAsyncBlock.invoke(this)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                } catch (e: SocketTimeoutException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private inline fun <T> doCoroutinesWorkWithProgressBar(
        crossinline doOnAsyncBlock: suspend CoroutineScope.() -> T,
        coroutineScope: CoroutineScope,
        context: CoroutineContext
    ) {
        coroutineScope.launch {
            withContext(context) {
                _progressBarState.update { true }
                try {
                    doOnAsyncBlock.invoke(this)
                } catch (e: UnknownHostException) {
                    e.printStackTrace()
                } catch (e: SocketTimeoutException) {
                    e.printStackTrace()
                } finally {
                    _progressBarState.update { false }
                }
            }
        }
    }
}