package ru.santaev.gradle_metrics_plugin.collector

import ru.santaev.gradle_metrics_plugin.api.Metric
import ru.santaev.gradle_metrics_plugin.api.MetricProcessorId
import ru.santaev.gradle_metrics_plugin.api.MetricUnit
import ru.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import ru.santaev.gradle_metrics_plugin.utils.logger

@MetricProcessorId("Custom")
class CustomMetricCollector: BaseMetricCollector() {

    private val logger = logger(this)

    init {
        afterBuild {
            val metricId = config.properties["metricId"]
            val value = config.properties["value"]

            if (metricId == null) {
                project.logger.error("No \"metricId\" in Custom collector")
                return@afterBuild
            }
            if (value == null) {
                project.logger.error("No \"value\" in Custom collector")
                return@afterBuild
            }
            collect(
                Metric(metricId, value, MetricUnit.None)
            )
        }
    }
}
