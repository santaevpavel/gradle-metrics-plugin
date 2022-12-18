package com.santaev.gradle_metrics_plugin.api.collector

import com.santaev.gradle_metrics_plugin.api.Metric

abstract class SimpleMetricCollector(
    private val collector: Environment.() -> Metric
) : BaseMetricCollector() {

    init {
        afterBuild {
            collect(collector.invoke(this))
        }
    }
}