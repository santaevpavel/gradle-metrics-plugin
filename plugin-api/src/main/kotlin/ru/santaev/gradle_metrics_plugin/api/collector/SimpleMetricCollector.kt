package ru.santaev.gradle_metrics_plugin.api.collector

import ru.santaev.gradle_metrics_plugin.api.Metric

abstract class SimpleMetricCollector(
    private val collector: BaseMetricCollector.Environment.() -> Metric
) : BaseMetricCollector() {

    init {
        afterBuild {
            collect(collector.invoke(this))
        }
    }
}