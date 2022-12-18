package com.santaev.gradle_metrics_plugin.collector

import com.santaev.gradle_metrics_plugin.api.Metric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.MetricUnit
import com.santaev.gradle_metrics_plugin.api.collector.SimpleMetricCollector

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