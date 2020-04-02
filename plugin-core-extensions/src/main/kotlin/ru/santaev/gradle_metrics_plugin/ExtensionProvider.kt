package ru.santaev.gradle_metrics_plugin

import ru.santaev.gradle_metrics_plugin.api.IExtensionsProvider
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.collector.*
import ru.santaev.gradle_metrics_plugin.dispatcher.ConsoleMetricsDispatcher

@Suppress("unused")
class ExtensionProvider: IExtensionsProvider {

    override fun provideCollectors(): List<IMetricsCollector> {
        return listOf(
            JUnitTestMetricsCollector(),
            BuildTimeMetricCollector(),
            JarFileSizeMetricCollector(),
            TasksCountMetricCollector(),
            ConfigurableFileSizeMetricCollector()
        )
    }

    override fun provideDispatcher(): List<IMetricsDispatcher> {
        return listOf(ConsoleMetricsDispatcher())
    }
}