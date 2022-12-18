package com.santaev.gradle_metrics_plugin.api.collector

import org.gradle.api.Project
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.IMetricsStore

interface IMetricsCollector {

    fun init(config: Config, metricsStore: IMetricsStore, project: Project)
}

