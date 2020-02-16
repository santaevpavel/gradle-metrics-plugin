package ru.santaev.gradle_metrics_plugin.dispatchers

import ru.santaev.gradle_metrics_plugin.Metric

interface IMetricsDispatcher {

    fun dispatch(metrics: List<Metric>)
}