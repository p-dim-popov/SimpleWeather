package com.example.simpleweather.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

fun <TThrottleParam> throttleLatest(
    intervalMs: Long = 300L,
    coroutineScope: CoroutineScope,
    destinationFunction: (TThrottleParam) -> Unit
): (TThrottleParam) -> Unit {
    var throttleJob: Job? = null
    var latestParam: TThrottleParam
    return {
        latestParam = it
        if (throttleJob?.isCompleted != false) {
            throttleJob = coroutineScope.launch {
                delay(intervalMs)
                latestParam.let(destinationFunction)
            }
        }
    }
}
