package com.santaev.gradle_metrics_plugin.collector

import com.santaev.gradle_metrics_plugin.api.LongMetric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.MetricUnit
import com.santaev.gradle_metrics_plugin.api.StringMetric
import com.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector

@MetricProcessorId("ProjectProperties")
class ProjectMetricCollector : BaseMetricCollector() {

    init {
        afterBuild {
            collectProjectName()
            collectModulesCount()
        }
    }

    private fun collectProjectName() {
        collect(
            StringMetric(
                id = "ProjectName",
                value = project?.name.orEmpty(),
                unit = MetricUnit.None
            )
        )
        collect(
            StringMetric(
                id = "RootProjectName",
                value = project?.rootProject?.name.orEmpty(),
                unit = MetricUnit.None
            )
        )
    }

    private fun collectModulesCount() {
        collect(
            LongMetric(
                id = "ModulesCount",
                value = project?.rootProject?.subprojects?.size?.toLong() ?: 0L,
                unit = MetricUnit.None
            )
        )
    }
}