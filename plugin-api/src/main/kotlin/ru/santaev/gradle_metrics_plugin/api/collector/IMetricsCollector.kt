package ru.santaev.gradle_metrics_plugin.api.collector

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.IMetricsStore

interface IMetricsCollector {

    fun init(metricsStore: IMetricsStore, project: Project)
}

