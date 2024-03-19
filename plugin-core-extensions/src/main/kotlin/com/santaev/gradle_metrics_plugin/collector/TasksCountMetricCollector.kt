package com.santaev.gradle_metrics_plugin.collector

import com.santaev.gradle_metrics_plugin.api.*
import com.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import com.santaev.gradle_metrics_plugin.utils.logger
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.task.TaskFailureResult
import org.gradle.tooling.events.task.TaskFinishEvent
import org.gradle.tooling.events.task.TaskSkippedResult
import org.gradle.tooling.events.task.TaskSuccessResult

@MetricProcessorId("TasksCount")
class TasksCountMetricCollector : BaseMetricCollector() {

    init {
        afterInit {
            val tasksCounter = TasksCounter()
            val tasksCounterProvider = project.provider { tasksCounter }
            plugin.getBuildEventsListenerRegistry().onTaskCompletion(tasksCounterProvider)

            afterBuild {
                collectMetrics(tasksCounter)
            }
        }
    }

    private fun collectMetrics(tasksCounter: TasksCounter) {
        metricsStore?.add(
            LongMetric(
                id = TASKS_COUNT_METRIC_ID,
                value = tasksCounter.tasksCount.toLong(),
                unit = MetricUnit.None
            )
        )
        metricsStore?.add(
            LongMetric(
                id = SUCCESSFUL_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.successfulTaskCount.toLong(),
                unit = MetricUnit.None
            )
        )
        metricsStore?.add(
            LongMetric(
                id = UP_TO_DATE_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.upToDateTaskCount.toLong(),
                unit = MetricUnit.None
            )
        )
        metricsStore?.add(
            LongMetric(
                id = FAILED_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.failedTaskCount.toLong(),
                unit = MetricUnit.None
            )
        )
        metricsStore?.add(
            LongMetric(
                id = SKIPPED_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.skippedTaskCount.toLong(),
                unit = MetricUnit.None
            )
        )
    }

    private class TasksCounter: OperationCompletionListener {

        var tasksCount = 0
        var successfulTaskCount = 0
        var upToDateTaskCount = 0
        var fromCacheTaskCount = 0
        var skippedTaskCount = 0
        var failedTaskCount = 0

        override fun onFinish(event: FinishEvent) {
            if (event !is TaskFinishEvent) {
                return
            }
            when (val result = event.result) {
                is TaskSuccessResult -> {
                    tasksCount++
                    when {
                        result.isUpToDate -> upToDateTaskCount++
                        result.isFromCache -> fromCacheTaskCount++
                    }
                }
                is TaskFailureResult -> {
                    tasksCount++
                    failedTaskCount++
                }
                is TaskSkippedResult -> {
                    tasksCount++
                    skippedTaskCount++
                }
            }
        }
    }

    companion object {
        private const val TASKS_COUNT_METRIC_ID = "TasksCount"
        private const val SUCCESSFUL_TASKS_COUNT_METRIC_ID = "SuccessfulTasksCount"
        private const val UP_TO_DATE_TASKS_COUNT_METRIC_ID = "UpToDateTasksCount"
        private const val FAILED_TASKS_COUNT_METRIC_ID = "FailedTasksCount"
        private const val SKIPPED_TASKS_COUNT_METRIC_ID = "SkippedTasksCount"
    }
}


