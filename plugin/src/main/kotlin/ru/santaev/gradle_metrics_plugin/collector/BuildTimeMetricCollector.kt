package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import ru.santaev.gradle_metrics_plugin.IMetricsStore
import ru.santaev.gradle_metrics_plugin.Metric
import ru.santaev.gradle_metrics_plugin.MetricUnit
import ru.santaev.gradle_metrics_plugin.utils.logger

class BuildTimeMetricCollector: IMetricsCollector {

    override fun collect(metricsStore: IMetricsStore, project: Project) {
        project.gradle.addBuildListener(
            BuildTimeCalculatorListener { buildTimeMillis ->
                collectBuildTime(metricsStore, buildTimeMillis)
            }
        )
    }

    private fun collectBuildTime(metricsStore: IMetricsStore, buildTimeMillis: Long) {
        metricsStore.add(
            Metric(
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
): BuildListener, TaskExecutionListener {

    private val logger = logger(this)
    private var buildStartTime = 0L
    private var isBuildStarted = false

    override fun settingsEvaluated(settings: Settings) {
        logger.error("settingsEvaluated")
    }

    override fun buildFinished(result: BuildResult) {
        val buildTime = System.currentTimeMillis() - buildStartTime
        onBuildFinished(buildTime)
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun buildStarted(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
    }

    override fun beforeExecute(task: Task) {
        logger.warn("beforeExecute ${task.name}")
        if (!isBuildStarted) {
            isBuildStarted = true
            beforeFirstTaskExecute()
        }
    }

    override fun afterExecute(task: Task, state: TaskState) {
        logger.warn("settingsEvaluated ${task.name}")
    }

    private fun beforeFirstTaskExecute() {
        buildStartTime = System.currentTimeMillis()
    }
}