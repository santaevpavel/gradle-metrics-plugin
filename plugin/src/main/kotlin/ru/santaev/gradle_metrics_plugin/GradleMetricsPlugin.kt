package ru.santaev.gradle_metrics_plugin

import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.collector.BuildTimeMetricCollector
import ru.santaev.gradle_metrics_plugin.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.collector.TasksCountMetricCollector
import ru.santaev.gradle_metrics_plugin.dispatchers.ConsoleMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.dispatchers.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.utils.BuildListenerAdapter


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
            collector.init(metricsStore, project)
        }
        initBuildFinishListener(project)

    }

    private fun initBuildFinishListener(project: Project) {
        project.gradle.addListener(
            object : BuildListenerAdapter() {
                override fun buildFinished(result: BuildResult) {
                    dispatchMetrics()
                }
            }
        )
    }

    private fun dispatchMetrics() {
        dispatchers.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }
}
