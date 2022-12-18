package com.santaev.gradle_metrics_plugin.api

import com.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import com.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

interface IExtensionsProvider {

    fun provideCollectors(): List<Class<out IMetricsCollector>>

    fun provideDispatcher(): List<Class<out IMetricsDispatcher>>
}