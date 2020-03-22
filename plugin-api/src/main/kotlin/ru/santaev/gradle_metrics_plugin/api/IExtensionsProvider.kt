package ru.santaev.gradle_metrics_plugin.api

import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

interface IExtensionsProvider {

    fun provideCollectors(): List<IMetricsCollector>

    fun provideDispatcher(): List<IMetricsDispatcher>
}