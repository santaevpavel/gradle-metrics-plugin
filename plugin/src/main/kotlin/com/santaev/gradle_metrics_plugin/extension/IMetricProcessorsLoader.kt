package com.santaev.gradle_metrics_plugin.extension

import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import com.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import com.santaev.gradle_metrics_plugin.utils.logger
import java.io.Closeable
import java.io.File

interface IMetricProcessorsLoader : Closeable {

    fun load(jarFiles: List<File>): MetricProcessorsLoadInfo
}

class MetricProcessorsLoader(
    private val extensionsProviderJarLoader: ExtensionsProviderJarLoader = ExtensionsProviderJarLoader()
) : IMetricProcessorsLoader, Closeable by extensionsProviderJarLoader {

    private val logger = logger(this)

    override fun load(jarFiles: List<File>): MetricProcessorsLoadInfo {
        val extensionProviders = extensionsProviderJarLoader.load(jarFiles)
        return MetricProcessorsLoadInfo(
            collectors = getCollectorsLoadInfo(extensionProviders.flatMap { it.provideCollectors() }),
            dispatchers = getDispatchersLoadInfo(extensionProviders.flatMap { it.provideDispatcher() })
        ).also { logProcessors(it) }
    }

    private fun getCollectorsLoadInfo(
        collectorClasses: List<Class<out IMetricsCollector>>
    ): Map<String, MetricCollectorLoadInfo> {
        return collectorClasses
            .mapNotNull { collectorClass ->
                val annotation = collectorClass.findMetricProcessorAnnotation()
                if (annotation != null) {
                    annotation.id to collectorClass
                } else {
                    logger.warn("Collector ${collectorClass.name} doesn't have annotation `MetricProcessorId`")
                    null
                }
            }
            .map { (id, clazz) ->
                id to MetricCollectorLoadInfo(id, InstantiatorImpl(clazz))
            }
            .toMap()
    }

    private fun getDispatchersLoadInfo(
        dispatcherClasses: List<Class<out IMetricsDispatcher>>
    ): Map<String, MetricDispatcherLoadInfo> {
        return dispatcherClasses
            .mapNotNull { dispatcherClass ->
                val annotation = dispatcherClass.findMetricProcessorAnnotation()
                if (annotation != null) {
                    annotation.id to dispatcherClass
                } else {
                    logger.warn("Dispatcher ${dispatcherClass.name} doesn't have annotation `MetricProcessorId`")
                    null
                }
            }
            .map { (id, clazz) ->
                id to MetricDispatcherLoadInfo(id, InstantiatorImpl(clazz))
            }
            .toMap()
    }

    private fun Class<*>.findMetricProcessorAnnotation(): MetricProcessorId? {
        return annotations.firstOrNull { it.annotationClass.java == MetricProcessorId::class.java} as MetricProcessorId?
    }

    private fun logProcessors(processors: MetricProcessorsLoadInfo) {
        logger.info("Loaded processors:")
        processors.collectors.keys.forEach { collector ->
            logger.info("\t- $collector")
        }
        logger.info("Loaded dispatchers:")
        processors.dispatchers.keys.forEach { dispatcher ->
            logger.info("\t- $dispatcher")
        }
    }
}