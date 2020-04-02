package ru.santaev.gradle_metrics_plugin.api.collector

import org.gradle.BuildResult
import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.Config
import ru.santaev.gradle_metrics_plugin.api.IMetricsStore
import ru.santaev.gradle_metrics_plugin.utils.BuildListenerAdapter

abstract class BaseMetricCollector: IMetricsCollector {

    protected var metricsStore: IMetricsStore? = null
    protected var project: Project? = null
    protected var config: Config? = null

    override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        this.metricsStore = metricsStore
        this.project = project
        this.config = config
        project.gradle.addBuildListener(
            object : BuildListenerAdapter() {
                override fun buildFinished(result: BuildResult) {
                    onBuildFinish()
                }
            }
        )
    }

    protected open fun onBuildFinish() {
    }
}

