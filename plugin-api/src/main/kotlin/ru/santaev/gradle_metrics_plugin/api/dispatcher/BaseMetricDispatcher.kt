package ru.santaev.gradle_metrics_plugin.api.dispatcher

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.Config

abstract class BaseMetricDispatcher : IMetricsDispatcher {

    protected lateinit var project: Project
    protected lateinit var config: Config

    override fun init(config: Config, project: Project) {
        this.project = project
        this.config = config
    }
}

