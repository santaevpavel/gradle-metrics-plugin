package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import ru.santaev.gradle_metrics_plugin.*
import ru.santaev.gradle_metrics_plugin.utils.logger

class TasksCountMetricCollector : IMetricsCollector {

    private var metricsStore: IMetricsStore? = null

    override fun collect(metricsStore: IMetricsStore, project: Project) {
        this.metricsStore = metricsStore
        project.gradle.addBuildListener(TasksCountMetricCollectorListener())
    }

    private fun collectMetrics(listener: TasksCountMetricCollectorListener) {
        metricsStore?.add(
            LongMetric(
                id = TASKS_COUNT_METRIC_ID,
                value = listener.tasksCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
        metricsStore?.add(
            LongMetric(
                id = SUCCESSFUL_TASKS_COUNT_METRIC_ID,
                value = listener.successfulTaskCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
        metricsStore?.add(
            LongMetric(
                id = UP_TO_DATE_TASKS_COUNT_METRIC_ID,
                value = listener.upToDateTaskCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
        metricsStore?.add(
            LongMetric(
                id = FAILED_TASKS_COUNT_METRIC_ID,
                value = listener.failedTaskCount.toLong(),
                unit = MetricUnit.Pieces
            )
        )
    }

    private inner class TasksCountMetricCollectorListener : BuildListener, TaskExecutionListener {

        var tasksCount = 0
        var successfulTaskCount = 0
        var upToDateTaskCount = 0
        var failedTaskCount = 0
        private val logger = logger(this)


        override fun settingsEvaluated(settings: Settings) {
        }

        override fun buildFinished(result: BuildResult) {
            collectMetrics(this)
        }

        override fun projectsLoaded(gradle: Gradle) {
        }

        override fun buildStarted(gradle: Gradle) {
        }

        override fun projectsEvaluated(gradle: Gradle) {
        }

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
        private const val SUCCESSFUL_TASKS_COUNT_METRIC_ID = "SuccesfulTasksCount"
        private const val UP_TO_DATE_TASKS_COUNT_METRIC_ID = "UpToDateTasksCount"
        private const val FAILED_TASKS_COUNT_METRIC_ID = "FailedTasksCount"
    }
}


