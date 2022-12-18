package com.santaev.gradle_metrics_plugin

import com.santaev.gradle_metrics_plugin.api.IExtensionsProvider
import com.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import com.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import com.santaev.gradle_metrics_plugin.collector.*
import com.santaev.gradle_metrics_plugin.dispatcher.ConsoleMetricsDispatcher
import com.santaev.gradle_metrics_plugin.dispatcher.JsonFileMetricsDispatcher

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
            ProjectMetricCollector::class.java,
            CustomMetricCollector::class.java
        )
    }

    override fun provideDispatcher(): List<Class<out IMetricsDispatcher>> {
        return listOf(
            ConsoleMetricsDispatcher::class.java,
            JsonFileMetricsDispatcher::class.java
        )
    }
}