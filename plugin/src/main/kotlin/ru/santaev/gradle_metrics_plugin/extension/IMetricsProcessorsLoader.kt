package ru.santaev.gradle_metrics_plugin.extension

import ru.santaev.gradle_metrics_plugin.api.MetricProcessorId
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.utils.logger
import java.io.Closeable
import java.io.File

interface IMetricsProcessorsLoader : Closeable {

    fun load(jarFiles: List<File>): MetricProcessorsLoadInfo
}

class MetricsProcessorsLoader(
    private val extensionsLoader: ExtensionsLoader = ExtensionsLoader()
) : IMetricsProcessorsLoader, Closeable by extensionsLoader {

    private val logger = logger(this)

    override fun load(jarFiles: List<File>): MetricProcessorsLoadInfo {
        val extensionProviders = extensionsLoader.load(jarFiles)
        return MetricProcessorsLoadInfo(
            collectors = getCollectorsLoadInfo(extensionProviders.flatMap { it.provideCollectors() }),
            dispatchers = getDispatchersLoadInfo(extensionProviders.flatMap { it.provideDispatcher() })
        )
    }

    private fun getCollectorsLoadInfo(
        collectorClasses: List<Class<out IMetricsCollector>>
    ): Map<String, MetricCollectorLoadInfo> {
        return collectorClasses
            .map { collectorClass ->
                val annotation = collectorClass.annotations.firstOrNull { a ->
                    (a.annotationClass.java == MetricProcessorId::class.java)
                }
                if (annotation != null) {
                    (annotation as MetricProcessorId).id to collectorClass
                } else {
                    logger.warn("Collector ${collectorClass.name} doesn't have annotation `MetricProcessorId`")
                    null to collectorClass
                }
            }
            .filter { (id, _) -> id != null }
            .map { (id, clazz) ->
                id!! to MetricCollectorLoadInfo(id, InstantiatorImpl(clazz))
            }
            .toMap()
    }

    private fun getDispatchersLoadInfo(
        dispatcherClasses: List<Class<out IMetricsDispatcher>>
    ): Map<String, MetricDispatcherLoadInfo> {
        return dispatcherClasses
            .map { dispatcherClass ->
                val annotation = dispatcherClass.annotations.firstOrNull { a ->
                    (a.annotationClass.java == MetricProcessorId::class.java)
                }
                if (annotation != null) {
                    (annotation as MetricProcessorId).id to dispatcherClass
                } else {
                    logger.warn("Dispatcher ${dispatcherClass.name} doesn't have annotation `MetricProcessorId`")
                    null to dispatcherClass
                }
            }
            .filter { (id, _) -> id != null }
            .map { (id, clazz) ->
                id!! to MetricDispatcherLoadInfo(id, InstantiatorImpl(clazz))
            }
            .toMap()
    }
}