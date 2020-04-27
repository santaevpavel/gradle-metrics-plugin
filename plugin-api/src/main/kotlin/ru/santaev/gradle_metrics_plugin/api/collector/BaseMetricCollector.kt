package ru.santaev.gradle_metrics_plugin.api.collector

import org.gradle.BuildResult
import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.Config
import ru.santaev.gradle_metrics_plugin.api.IMetricsStore
import ru.santaev.gradle_metrics_plugin.api.Metric
import ru.santaev.gradle_metrics_plugin.utils.BuildListenerAdapter

abstract class BaseMetricCollector : IMetricsCollector {

    protected var metricsStore: IMetricsStore? = null
    protected var project: Project? = null
    protected var config: Config? = null
    private val buildFinishListeners = mutableListOf<Environment.() -> Unit>()
    private val initListeners = mutableListOf<Environment.() -> Unit>()

    override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        this.metricsStore = metricsStore
        this.project = project
        this.config = config
        val environment = Environment(metricsStore, project, config)
        initListeners.forEach { it.invoke(environment) }
        project.gradle.addBuildListener(
            object : BuildListenerAdapter() {
                override fun buildFinished(result: BuildResult) {
                    buildFinishListeners.forEach { it.invoke(environment) }
                    onBuildFinish()
                }
            }
        )
    }

    fun addBuildFinishedListener(listener: Environment.() -> Unit) {
        buildFinishListeners.add(listener)
    }

    fun addInitListener(listener: Environment.() -> Unit) {
        initListeners.add(listener)
    }

    fun afterBuild(block: Environment.() -> Unit) {
        addBuildFinishedListener(block)
    }

    fun afterInit(block: Environment.() -> Unit) {
        addInitListener(block)
    }

    fun collect(metric: Metric) {
        metricsStore?.add(metric)
    }

    protected open fun onBuildFinish() {
    }

    data class Environment(
        val metricsStore: IMetricsStore,
        val project: Project,
        val config: Config
    )
}

