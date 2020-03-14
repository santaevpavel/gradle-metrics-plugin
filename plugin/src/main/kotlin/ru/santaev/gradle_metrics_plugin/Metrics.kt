package ru.santaev.gradle_metrics_plugin

import ru.santaev.gradle_metrics_plugin.api.IMetricsStore
import ru.santaev.gradle_metrics_plugin.api.Metric


class MetricsStore : IMetricsStore {

    private val metrics = mutableListOf<Metric>()

    fun getMetrics(): List<Metric> = metrics

    override fun add(metric: Metric) {
        metrics.add(metric)
    }
}