package ru.santaev.gradle_metrics_plugin.api.collector

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.Config
import ru.santaev.gradle_metrics_plugin.api.IMetricsStore

interface IMetricsCollector {

    val id: String

    fun init(config: Config, metricsStore: IMetricsStore, project: Project)
}

