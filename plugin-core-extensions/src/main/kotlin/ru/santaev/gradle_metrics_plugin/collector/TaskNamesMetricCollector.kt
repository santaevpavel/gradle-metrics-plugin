package ru.santaev.gradle_metrics_plugin.collector

import ru.santaev.gradle_metrics_plugin.api.Metric
import ru.santaev.gradle_metrics_plugin.api.MetricProcessorId
import ru.santaev.gradle_metrics_plugin.api.MetricUnit
import ru.santaev.gradle_metrics_plugin.api.collector.SimpleMetricCollector

@MetricProcessorId("TaskNames")
class TaskNamesMetricCollector : SimpleMetricCollector(
    collector = {
        Metric(
            id = "TaskNames",
            value = project.gradle.startParameter.taskNames.joinToString(),
            unit = MetricUnit.None
        )
    }
)