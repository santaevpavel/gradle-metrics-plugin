package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import ru.santaev.gradle_metrics_plugin.api.DoubleMetric
import ru.santaev.gradle_metrics_plugin.api.IMetricsStore
import ru.santaev.gradle_metrics_plugin.api.MetricUnit
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.utils.BuildListenerAdapter
import ru.santaev.gradle_metrics_plugin.utils.logger

class BuildTimeMetricCollector: IMetricsCollector {

    override val id: String = BUILD_TIME_METRIC_ID
    private val logger = logger(this)

    override fun init(metricsStore: IMetricsStore, project: Project) {
        project.gradle.addBuildListener(
            BuildTimeCalculatorListener { buildTimeMillis ->
                collectBuildTime(metricsStore, buildTimeMillis)
            }
        )
    }

    private fun collectBuildTime(metricsStore: IMetricsStore, buildTimeMillis: Long) {
        metricsStore.add(
            DoubleMetric(
                id = BUILD_TIME_METRIC_ID,
                value = buildTimeMillis / 1000.0,
                unit = MetricUnit.Seconds
            )
        )
    }

    companion object {
        private const val BUILD_TIME_METRIC_ID = "BuildTime"
    }
}


private class BuildTimeCalculatorListener(
    private val onBuildFinished: (buildTimeMillis: Long) -> Unit
): BuildListenerAdapter(), TaskExecutionListener {

    private var buildStartTime = 0L
    private var isBuildStarted = false

    override fun buildFinished(result: BuildResult) {
        val buildTime = System.currentTimeMillis() - buildStartTime
        onBuildFinished(buildTime)
    }

    override fun beforeExecute(task: Task) {
        if (!isBuildStarted) {
            isBuildStarted = true
            beforeFirstTaskExecute()
        }
    }

    override fun afterExecute(task: Task, state: TaskState) {
    }

    private fun beforeFirstTaskExecute() {
        buildStartTime = System.currentTimeMillis()
    }
}