package ru.santaev.gradle_metrics_plugin.extension

import java.io.File

interface IMetricsProcessorsLoader {

    fun load(jarFiles: List<File>): MetricProcessors
}

class MetricsProcessorsLoader: IMetricsProcessorsLoader {

    private val extensionsLoader = ExtensionsLoader()

    override fun load(jarFiles: List<File>): MetricProcessors {
        val extensionProviders = extensionsLoader.load(jarFiles)
        return MetricProcessors(
            collectors = extensionProviders.flatMap { it.provideCollectors() },
            dispatchers = extensionProviders.flatMap { it.provideDispatcher() }
        )
    }
}