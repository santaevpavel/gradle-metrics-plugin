package ru.santaev.gradle_metrics_plugin.amplitude

import ru.santaev.gradle_metrics_plugin.amplitude.dispatcher.AmplitudeMetricsDispatcher
import ru.santaev.gradle_metrics_plugin.api.IExtensionsProvider
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

@Suppress("unused")
class ExtensionProvider: IExtensionsProvider {

    override fun provideCollectors(): List<Class<out IMetricsCollector>> {
        return emptyList()
    }

    override fun provideDispatcher(): List<Class<out IMetricsDispatcher>> {
        return listOf(
            AmplitudeMetricsDispatcher::class.java
        )
    }
}