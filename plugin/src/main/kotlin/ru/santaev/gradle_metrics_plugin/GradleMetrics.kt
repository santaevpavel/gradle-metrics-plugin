package ru.santaev.gradle_metrics_plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import ru.santaev.gradle_metrics_plugin.api.Config
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
        logCollectorsAndDispatchers()
        project.whenBuildFinished { dispatchMetrics() }
    }


    private fun loadProcessors() {
        val loadedProcessors = metricsProcessorsLoader.load(pluginClasspath.toCollection(mutableListOf()))
        val collectorsToConfig = loadCollectors(loadedProcessors.collectors)
        processors = MetricProcessors(
            collectors = collectorsToConfig.map { it.first },
            dispatchers = loadedProcessors.dispatchers
        )
        initCollectors(collectorsToConfig)
        logNotExistingCollectors(loadedProcessors.collectors)
    }

    private fun loadCollectors(collectors: List<IMetricsCollector>): List<Pair<IMetricsCollector, Config>> {
        val idToCollectors = collectors
            .map { it.id.toLowerCase() to it }
            .toMap()

        return extension.collectors
            .map { collector -> idToCollectors[collector.id.toLowerCase()] to collector.properties}
            .filter { (collector, _) -> collector != null }
            .map { (collector, properties) -> collector as IMetricsCollector to Config(properties ?: emptyMap()) }
    }

    private fun initCollectors(collectorsToConfig: List<Pair<IMetricsCollector, Config>>) {
        collectorsToConfig.forEach { (collector, config) ->
            collector.init(config, metricsStore, project)
        }
    }

    private fun dispatchMetrics() {
        processors?.dispatchers?.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }

    private fun logCollectorsAndDispatchers() {
        extension.collectors.forEach {
            println("${it.id} -> ${it.properties}")
        }
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
        extension.collectors.forEach { collector ->
            if (ids.none { it.equals(collector.id, ignoreCase = true) }) {
                logger.error("Unknown collector with id = \"${collector.id}.\"")
                isAnyNonExistingCollector = true
            }
        }
        if (isAnyNonExistingCollector) {
            logger.error("To see available collectors run task with option --info.")
        }
    }
}