package ru.santaev.gradle_metrics_plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.extension.IMetricsProcessorsLoader
import ru.santaev.gradle_metrics_plugin.extension.MetricProcessors
import ru.santaev.gradle_metrics_plugin.utils.logger
import ru.santaev.gradle_metrics_plugin.utils.whenBuildFinished
import ru.santaev.gradle_metrics_plugin.utils.whenEvaluated

class GradleMetrics(
    private val project: Project,
    private val extension: GradleMetricsPluginExtension,
    @Classpath private val pluginClasspath: ConfigurableFileCollection,
    private val metricsProcessorsLoader: IMetricsProcessorsLoader
) {

    private val logger = logger(this)
    private val metricsStore = MetricsStore()
    private var processors: MetricProcessors? = null

    init {
        project.whenEvaluated { initMetricProcessors() }
    }


    private fun initMetricProcessors() {
        logger.info("Loading metrics processors")
        loadProcessors()
        initProcessors()
        logCollectorsAndDispatchers()
        project.whenBuildFinished { dispatchMetrics() }
    }


    private fun loadProcessors() {
        val loadedProcessors = metricsProcessorsLoader.load(pluginClasspath.toCollection(mutableListOf()))
        processors = MetricProcessors(
            collectors = loadedProcessors.collectors.filter { isCollectorEnabledInBuildConfig(it.id) },
            dispatchers = loadedProcessors.dispatchers
        )
        logNotExistingCollectors(loadedProcessors.collectors)
    }

    private fun initProcessors() {
        processors?.collectors?.forEach { collector ->
            collector.init(metricsStore, project)
        }
    }

    private fun dispatchMetrics() {
        processors?.dispatchers?.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }


    private fun isCollectorEnabledInBuildConfig(collectorId: String): Boolean {
        return extension.collectorsCreationDsl.collectorConfigurations
            .any { it.id.equals(collectorId, ignoreCase = true) }
    }

    private fun logCollectorsAndDispatchers() {
        val log = buildString {
            appendln("Metric collectors:")
            processors?.collectors?.forEach { collector ->
                appendln("\t- ${collector::class.java.simpleName}")
            }
            appendln("Metric dispatchers:")
            processors?.dispatchers?.forEach { dispatcher ->
                appendln("\t- ${dispatcher::class.java.simpleName}")
            }
        }
        logger.info(log)
        println(log)
    }

    private fun logNotExistingCollectors(allCollectors: List<IMetricsCollector>) {
        val ids = allCollectors.map { it.id }
        var isAnyNonExistingCollector = false
        extension.collectorsCreationDsl.collectorConfigurations.forEach { collectorId ->
            if (ids.none { it.equals(collectorId.id, ignoreCase = true) }) {
                logger.error("Unknown collector with id = \"${collectorId.id}.\"")
                isAnyNonExistingCollector = true
            }
        }
        if (isAnyNonExistingCollector) {
            logger.error("To see available collectors run task with option --info.")
        }
    }
}