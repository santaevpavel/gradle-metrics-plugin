package ru.santaev.gradle_metrics_plugin.collector

import ru.santaev.gradle_metrics_plugin.api.*
import ru.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector

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