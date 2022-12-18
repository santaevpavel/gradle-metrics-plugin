package com.santaev.gradle_metrics_plugin

import com.santaev.gradle_metrics_plugin.api.IMetricsStore
import com.santaev.gradle_metrics_plugin.api.Metric


class MetricsStore : IMetricsStore {

    private val metrics = mutableListOf<Metric>()

    fun getMetrics(): List<Metric> = metrics

    override fun add(metric: Metric) {
        metrics.add(metric)
    }
}