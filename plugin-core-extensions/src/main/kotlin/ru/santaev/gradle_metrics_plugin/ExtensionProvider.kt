package ru.santaev.gradle_metrics_plugin

import ru.santaev.gradle_metrics_plugin.api.IExtensionsProvider
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.collector.*
import ru.santaev.gradle_metrics_plugin.dispatcher.ConsoleMetricsDispatcher

@Suppress("unused")
class ExtensionProvider: IExtensionsProvider {

    override fun provideCollectors(): List<Class<out IMetricsCollector>> {
        return listOf(
            JUnitTestMetricsCollector::class.java,
            BuildMetricCollector::class.java,
            JarFileSizeMetricCollector::class.java,
            TasksCountMetricCollector::class.java,
            ConfigurableFileSizeMetricCollector::class.java,
            TaskNamesMetricCollector::class.java,
            ProjectMetricCollector::class.java
        )
    }

    override fun provideDispatcher(): List<Class<out IMetricsDispatcher>> {
        return listOf(
            ConsoleMetricsDispatcher::class.java
        )
    }
}