package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import ru.santaev.gradle_metrics_plugin.api.IMetricsStore
import ru.santaev.gradle_metrics_plugin.api.LongMetric
import ru.santaev.gradle_metrics_plugin.api.MetricUnit
import ru.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import ru.santaev.gradle_metrics_plugin.utils.logger

class TasksCountMetricCollector : BaseMetricCollector() {

    override val id: String = TASKS_COUNT_METRIC_ID
    private val logger = logger(this)
    private var tasksCounter: TasksCountMetricCollectorListener? = null

    override fun init(metricsStore: IMetricsStore, project: Project) {
        super.init(metricsStore, project)
        tasksCounter = TasksCountMetricCollectorListener().also { listener ->
            project.gradle.addListener(listener)
        }
    }

    override fun onBuildFinish() {
        super.onBuildFinish()
        collectMetrics()
    }

    private fun collectMetrics() {
        val tasksCounter = tasksCounter ?: return
        metricsStore?.add(
            LongMetric(
                id = TASKS_COUNT_METRIC_ID,
                value = tasksCounter.tasksCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
        metricsStore?.add(
            LongMetric(
                id = SUCCESSFUL_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.successfulTaskCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
        metricsStore?.add(
            LongMetric(
                id = UP_TO_DATE_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.upToDateTaskCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
        metricsStore?.add(
            LongMetric(
                id = FAILED_TASKS_COUNT_METRIC_ID,
                value = tasksCounter.failedTaskCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
    }

    private inner class TasksCountMetricCollectorListener : TaskExecutionListener {

        var tasksCount = 0
        var successfulTaskCount = 0
        var upToDateTaskCount = 0
        var failedTaskCount = 0

        override fun beforeExecute(task: Task) {
            tasksCount++
        }

        override fun afterExecute(task: Task, state: TaskState) {
            when {
                state.upToDate -> upToDateTaskCount++
                state.failure != null -> failedTaskCount++
                else -> successfulTaskCount++
            }
        }
    }

    companion object {
        private const val TASKS_COUNT_METRIC_ID = "TasksCount"
        private const val SUCCESSFUL_TASKS_COUNT_METRIC_ID = "SuccessfulTasksCount"
        private const val UP_TO_DATE_TASKS_COUNT_METRIC_ID = "UpToDateTasksCount"
        private const val FAILED_TASKS_COUNT_METRIC_ID = "FailedTasksCount"
    }
}


