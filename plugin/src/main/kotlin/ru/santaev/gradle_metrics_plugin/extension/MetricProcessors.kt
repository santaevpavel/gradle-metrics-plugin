package ru.santaev.gradle_metrics_plugin.extension

import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

data class MetricProcessors(
    val collectors: List<IMetricsCollector>,
    val dispatchers: List<IMetricsDispatcher>
)