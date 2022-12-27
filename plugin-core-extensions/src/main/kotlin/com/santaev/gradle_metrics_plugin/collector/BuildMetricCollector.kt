package com.santaev.gradle_metrics_plugin.collector

import com.santaev.gradle_metrics_plugin.api.DoubleMetric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.MetricUnit
import com.santaev.gradle_metrics_plugin.api.StringMetric
import org.gradle.BuildResult
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.tasks.TaskState
import com.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import com.santaev.gradle_metrics_plugin.utils.BuildListenerAdapter
import com.santaev.gradle_metrics_plugin.utils.logger

@MetricProcessorId("Build")
class BuildMetricCollector: BaseMetricCollector() {

    private val logger = logger(this)

    init {
        afterInit {
            project.gradle.addBuildListener(
                BuildTimeCalculatorListener { buildTimeMillis ->
                    collectBuildTime(buildTimeMillis)
                }
            )
        }
        afterBuild { result ->
            collectStatus(result)
        }
    }

    private fun collectBuildTime(buildTimeMillis: Long) {
        metricsStore?.add(
            DoubleMetric(
                id = BUILD_TIME_METRIC_ID,
                value = buildTimeMillis / 1000.0,
                unit = MetricUnit.Seconds
            )
        )
    }

    private fun collectStatus(result: BuildResult) {
        metricsStore?.add(
            StringMetric(
                id = BUILD_STATUS_METRIC_ID,
                value = if (result.failure == null) "Successful" else "Failed",
                unit = MetricUnit.None
            )
        )
    }

    companion object {
        private const val BUILD_TIME_METRIC_ID = "BuildTime"
        private const val BUILD_STATUS_METRIC_ID = "BuildStatus"
    }
}


private class BuildTimeCalculatorListener(
    private val onBuildFinished: (buildTimeMillis: Long) -> Unit
): BuildListenerAdapter(), TaskExecutionListener {

    private var buildStartTime = 0L
    private var isBuildStarted = false

    @Deprecated("Deprecated in Java")
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