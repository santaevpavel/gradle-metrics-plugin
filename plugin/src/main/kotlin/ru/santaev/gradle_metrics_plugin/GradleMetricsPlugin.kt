package ru.santaev.gradle_metrics_plugin

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.execution.TaskExecutionListener
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.TaskState
import ru.santaev.gradle_metrics_plugin.collector.BuildTimeMetricCollector
import ru.santaev.gradle_metrics_plugin.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.collector.TasksCountMetricCollector
import ru.santaev.gradle_metrics_plugin.dispatchers.ConsoleMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.dispatchers.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.utils.logger


@Suppress("UnstableApiUsage")
class GradleMetricsPlugin : Plugin<Project> {

    private val metricsStore = MetricsStore()
    private val dispatchers = listOf<IMetricsDispatcher>(
        ConsoleMetricsDispatcher()
    )
    private val collectors = listOf<IMetricsCollector>(
        BuildTimeMetricCollector(),
        TasksCountMetricCollector()
    )

    override fun apply(project: Project) {
        collectors.forEach { collector ->
            collector.collect(metricsStore, project)
        }
        initBuildFinishListener(project)

    }

    private fun initBuildFinishListener(project: Project) {
        project.gradle.addListener(
            BuildFinishListenerAdapter { dispatchMetrics() }
        )
    }

    private fun dispatchMetrics() {
        dispatchers.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }
}

private class BuildFinishListenerAdapter(
    private val onBuildFinishCallback: (BuildResult) -> Unit
) : BuildListener {

    override fun settingsEvaluated(settings: Settings) {
    }

    override fun buildFinished(result: BuildResult) {
        onBuildFinishCallback(result)
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun buildStarted(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
    }
}