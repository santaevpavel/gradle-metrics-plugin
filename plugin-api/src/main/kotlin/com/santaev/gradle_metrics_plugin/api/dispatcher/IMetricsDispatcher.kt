package com.santaev.gradle_metrics_plugin.api.dispatcher

import org.gradle.api.Project
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.Metric
import com.santaev.gradle_metrics_plugin.api.Plugin

interface IMetricsDispatcher {

    fun init(config: Config, project: Project, plugin: Plugin)

    fun dispatch(metrics: List<Metric>)
}