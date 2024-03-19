package com.santaev.gradle_metrics_plugin.api.dispatcher

import org.gradle.api.Project
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.Plugin

abstract class BaseMetricDispatcher : IMetricsDispatcher {

    protected lateinit var project: Project
    protected lateinit var config: Config
    protected lateinit var plugin: Plugin

    override fun init(config: Config, project: Project, plugin: Plugin) {
        this.project = project
        this.config = config
        this.plugin = plugin
    }
}

