package ru.santaev.gradle_metrics_plugin.api.dispatcher

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.Config
import ru.santaev.gradle_metrics_plugin.api.Metric

interface IMetricsDispatcher {

    fun init(config: Config, project: Project)

    fun dispatch(metrics: List<Metric>)
}