package ru.santaev.gradle_metrics_plugin.api

import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

interface IExtensionsProvider {

    fun provideCollectors(): List<Class<out IMetricsCollector>>

    fun provideDispatcher(): List<Class<out IMetricsDispatcher>>
}