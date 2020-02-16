package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.IMetricsStore

interface IMetricsCollector {

    fun collect(metricsStore: IMetricsStore, project: Project)
}
