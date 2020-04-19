package ru.santaev.gradle_metrics_plugin.extension

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.GradleMetricsPluginExtension
import ru.santaev.gradle_metrics_plugin.MetricsStore
import ru.santaev.gradle_metrics_plugin.api.Config
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.utils.logger

interface IMetricProcessorsConfigurator {

    fun configure(
        metricProcessorsLoadInfo: MetricProcessorsLoadInfo,
        configuration: GradleMetricsPluginExtension,
        metricsStore: MetricsStore,
        project: Project
    ): MetricProcessors
}

class MetricProcessorConfigurator : IMetricProcessorsConfigurator {

    private val logger = logger(this)

    override fun configure(
        metricProcessorsLoadInfo: MetricProcessorsLoadInfo,
        configuration: GradleMetricsPluginExtension,
        metricsStore: MetricsStore,
        project: Project
    ): MetricProcessors {
        val collectorsWithConfig = loadCollectors(metricProcessorsLoadInfo.collectors, configuration)
        val dispatchersWithConfig = loadDispatchers(metricProcessorsLoadInfo.dispatchers, configuration)

        val processors = MetricProcessors(
            collectors = collectorsWithConfig.map { it.first },
            dispatchers = dispatchersWithConfig.map { it.first }
        )
        initCollectors(collectorsWithConfig, metricsStore, project)
        initDispatchers(dispatchersWithConfig, project)
        logNotExistingCollectors(metricProcessorsLoadInfo.collectors, configuration)
        logCollectorsAndDispatchers(processors)
        return processors
    }

    private fun loadCollectors(
        collectors: Map<String, MetricCollectorLoadInfo>,
        configuration: GradleMetricsPluginExtension
    ): List<Pair<IMetricsCollector, Config>> {
        val idToCollectors = collectors.mapKeys { it.key.toLowerCase() }
        return configuration.collectors
            .map { collector ->
                idToCollectors[collector.id.toLowerCase()] to collector.properties
            }
            .filter { (collector, _) -> collector != null }
            .map { (collector, properties) ->
                (collector as MetricCollectorLoadInfo).instantiator.instantiate() to Config(properties ?: emptyMap())
            }
    }

    private fun loadDispatchers(
        dispatchers: Map<String, MetricDispatcherLoadInfo>,
        configuration: GradleMetricsPluginExtension
    ): List<Pair<IMetricsDispatcher, Config>> {
        val idToDispatchers = dispatchers.mapKeys { it.key.toLowerCase() }
        return configuration.dispatchers
            .map { dispatcher ->
                idToDispatchers[dispatcher.id.toLowerCase()] to dispatcher.properties
            }
            .filter { (dispatcher, _) -> dispatcher != null }
            .map { (dispatcher, properties) ->
                (dispatcher as MetricDispatcherLoadInfo).instantiator.instantiate() to Config(properties ?: emptyMap())
            }
    }

    private fun initCollectors(
        collectorsToConfig: List<Pair<IMetricsCollector, Config>>,
        metricsStore: MetricsStore,
        project: Project
    ) {
        collectorsToConfig.forEach { (collector, config) ->
            collector.init(config, metricsStore, project)
        }
    }

    private fun initDispatchers(
        dispatchersToConfig: List<Pair<IMetricsDispatcher, Config>>,
        project: Project
    ) {
        dispatchersToConfig.forEach { (dispatcher, config) ->
            dispatcher.init(config, project)
        }
    }

    private fun logCollectorsAndDispatchers(processors: MetricProcessors) {
        val log = buildString {
            appendln("Metric processors:")
            processors.collectors.forEach { collector ->
                appendln("\t- ${collector::class.java.simpleName}")
            }
            appendln("Metric dispatchers:")
            processors.dispatchers.forEach { dispatcher ->
                appendln("\t- ${dispatcher::class.java.simpleName}")
            }
        }
        logger.info(log)
        println(log)
    }

    private fun logNotExistingCollectors(
        collectors: Map<String, MetricCollectorLoadInfo>,
        configuration: GradleMetricsPluginExtension
    ) {
        val ids = collectors.asSequence().map { it.key.toLowerCase() }.toList()
        var isAnyNonExistingCollector = false
        configuration.collectors.forEach { collector ->
            if (ids.none { it.equals(collector.id, ignoreCase = true) }) {
                logger.error("Unknown collector with id = \"${collector.id}\".")
                isAnyNonExistingCollector = true
            }
        }
        if (isAnyNonExistingCollector) {
            logger.error("To see available processors run task with option --info.")
        }
    }

}