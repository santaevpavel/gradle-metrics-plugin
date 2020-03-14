package ru.santaev.gradle_metrics_plugin

import org.gradle.BuildResult
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.invocation.Gradle
import org.gradle.api.tasks.Classpath
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatchers.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.collector.BuildTimeMetricCollector
import ru.santaev.gradle_metrics_plugin.collector.JUnitTestMetricsCollector
import ru.santaev.gradle_metrics_plugin.collector.JarFileSizeMetricCollector
import ru.santaev.gradle_metrics_plugin.collector.TasksCountMetricCollector
import ru.santaev.gradle_metrics_plugin.dispatchers.ConsoleMetricsDispatcher
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
    private lateinit var extension: GradleMetricsPluginExtension
    @Classpath
    private lateinit var pluginClasspath: ConfigurableFileCollection

    override fun apply(project: Project) {
        pluginClasspath = project.files()
        createConfiguration(project)
        extension = project.extensions.create(EXTENSION_NAME, GradleMetricsPluginExtension::class.java)
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
                    println("pluginClasspath2 ${pluginClasspath.toCollection(mutableListOf())}")
                }
            }
        )
    }

    private fun createConfiguration(project: Project) {
        val configuration = project.configurations.create("gradleMetricsPluginExtension") { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "Test"
        }
        pluginClasspath.setFrom(configuration)
    }

    private fun logCollectorsAndDispatchers() {
        println(
            "Configured metric collectors   " +
                    extension.collectorsCreationDsl.collectorConfigurations.joinToString(", ")
        )
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

    companion object {
        private const val EXTENSION_NAME = "metrics"
    }
}
