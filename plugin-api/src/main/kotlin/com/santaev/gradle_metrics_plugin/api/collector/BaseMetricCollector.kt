package com.santaev.gradle_metrics_plugin.api.collector

import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.IMetricsStore
import com.santaev.gradle_metrics_plugin.api.Metric
import com.santaev.gradle_metrics_plugin.api.Plugin
import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.flow.BuildWorkResult
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import kotlin.jvm.optionals.getOrNull

@Suppress("UnstableApiUsage")
abstract class BaseMetricCollector : IMetricsCollector {

    protected var metricsStore: IMetricsStore? = null
    protected var project: Project? = null
    protected var config: Config? = null
    protected var plugin: Plugin? = null
    private val buildFinishListeners = mutableListOf<Environment.(BuildResult) -> Unit>()
    private val initListeners = mutableListOf<Environment.() -> Unit>()

    @Suppress("UnstableApiUsage")
    override fun init(
        config: Config,
        metricsStore: IMetricsStore,
        project: Project,
        plugin: Plugin
    ) {
        this.metricsStore = metricsStore
        this.project = project
        this.config = config
        this.plugin = plugin

        val environment = Environment(metricsStore, project, config)
        initListeners.forEach { it.invoke(environment) }

        plugin.getFlowScope().always(
            BuildTimeListener::class.java
        ) { spec ->
            spec.parameters.collector.set(object : BuildTimeFinishListener {
                override fun onBuildFinish() {
                    onBuildFinish(plugin.getFlowProviders().buildWorkResult.get())
                }
            })
        }
    }

    private fun onBuildFinish(result: BuildWorkResult) {
        val environment = Environment(metricsStore!!, project!!, config!!)
        buildFinishListeners.forEach { it.invoke(environment, result.toBuildResult()) }
    }

    fun addBuildFinishedListener(listener: Environment.(BuildResult) -> Unit) {
        buildFinishListeners.add(listener)
    }

    fun addInitListener(listener: Environment.() -> Unit) {
        initListeners.add(listener)
    }

    fun afterBuild(block: Environment.(BuildResult) -> Unit) {
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

    @Suppress("UnstableApiUsage")
    private fun BuildWorkResult.toBuildResult(): BuildResult {
        return BuildResult(project!!.gradle, failure.getOrNull())
    }

    data class Environment(
        val metricsStore: IMetricsStore,
        val project: Project,
        val config: Config,
        val plugin: Plugin
    )

    private class BuildTimeListener : FlowAction<BuildTimeParameters> {

        override fun execute(parameters: BuildTimeParameters) {
            parameters.collector.get().onBuildFinish()
        }
    }

    interface BuildTimeParameters : FlowParameters {

        @get:Input
        val collector: Property<BuildTimeFinishListener>
    }

    interface BuildTimeFinishListener {
        fun onBuildFinish()
    }
}

