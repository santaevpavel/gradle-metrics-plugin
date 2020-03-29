package ru.santaev.gradle_metrics_plugin.extension

import java.io.Closeable
import java.io.File

interface IMetricsProcessorsLoader: Closeable {

    fun load(jarFiles: List<File>): MetricProcessors
}

class MetricsProcessorsLoader(
    private val extensionsLoader: ExtensionsLoader = ExtensionsLoader()
) : IMetricsProcessorsLoader, Closeable by extensionsLoader {

    override fun load(jarFiles: List<File>): MetricProcessors {
        val extensionProviders = extensionsLoader.load(jarFiles)
        return MetricProcessors(
            collectors = extensionProviders.flatMap { it.provideCollectors() },
            dispatchers = extensionProviders.flatMap { it.provideDispatcher() }
        )
    }
}