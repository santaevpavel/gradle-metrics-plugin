package ru.santaev.gradle_metrics_plugin.api.dispatcher

import ru.santaev.gradle_metrics_plugin.api.Metric

interface IMetricsDispatcher {

    fun dispatch(metrics: List<Metric>)
}