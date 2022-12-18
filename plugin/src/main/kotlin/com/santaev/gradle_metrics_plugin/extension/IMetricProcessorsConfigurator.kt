package com.santaev.gradle_metrics_plugin.extension

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import com.santaev.gradle_metrics_plugin.GradleMetricsPluginExtension
import com.santaev.gradle_metrics_plugin.MetricsStore
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import com.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

interface IMetricProcessorsConfigurator {

    fun configure(
        metricProcessorsLoadInfo: MetricProcessorsLoadInfo,
        configuration: GradleMetricsPluginExtension,
        metricsStore: MetricsStore,
        project: Project
    ): MetricProcessors
}

class MetricProcessorConfigurator : IMetricProcessorsConfigurator {

    private lateinit var logger: Logger

    override fun configure(
        metricProcessorsLoadInfo: MetricProcessorsLoadInfo,
        configuration: GradleMetricsPluginExtension,
        metricsStore: MetricsStore,
        project: Project
    ): MetricProcessors {
        logger = project.logger
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
            logger.info("Initializing ${collector::class.java.simpleName}. Config = $config")
            collector.init(config, metricsStore, project)
        }
    }

    private fun initDispatchers(
        dispatchersToConfig: List<Pair<IMetricsDispatcher, Config>>,
        project: Project
    ) {
        dispatchersToConfig.forEach { (dispatcher, config) ->
            logger.info("Initializing ${dispatcher::class.java.simpleName}. Config = $config")
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