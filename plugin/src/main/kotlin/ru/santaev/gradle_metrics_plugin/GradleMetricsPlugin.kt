package ru.santaev.gradle_metrics_plugin

import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle
import ru.santaev.gradle_metrics_plugin.collector.*
import ru.santaev.gradle_metrics_plugin.dispatchers.ConsoleMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.dispatchers.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.utils.BuildListenerAdapter
import ru.santaev.gradle_metrics_plugin.utils.logger


@Suppress("UnstableApiUsage")
class GradleMetricsPlugin : Plugin<Project> {

    private val logger = logger(this)

    private val metricsStore = MetricsStore()
    private val dispatchers = listOf<IMetricsDispatcher>(
        ConsoleMetricsDispatcher()
    )
    private val collectors = listOf<IMetricsCollector>(
        BuildTimeMetricCollector(),
        TasksCountMetricCollector(),
        JUnitTestMetricsCollector(),
        JarFileSizeMetricCollector()
    )

    override fun apply(project: Project) {
        collectors.forEach { collector ->
            collector.init(metricsStore, project)
        }
        initBuildListener(project)

    }

    private fun initBuildListener(project: Project) {
        project.gradle.addListener(
            object : BuildListenerAdapter() {
                override fun buildFinished(result: BuildResult) {
                    dispatchMetrics()
                }

                override fun projectsEvaluated(gradle: Gradle) {
                    super.projectsEvaluated(gradle)
                    logCollectorsAndDispatchers()
                }
            }
        )
    }

    private fun logCollectorsAndDispatchers() {
        val log = buildString {
            appendln("Metric collectors:")
            collectors.forEach { collector ->
                appendln("\t- ${collector::class.java.simpleName}")
            }
            appendln("Metric dispatchers:")
            dispatchers.forEach { dispatcher ->
                appendln("\t- ${dispatcher::class.java.simpleName}")
            }
        }
        logger.info(log)
    }

    private fun dispatchMetrics() {
        dispatchers.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }
}
