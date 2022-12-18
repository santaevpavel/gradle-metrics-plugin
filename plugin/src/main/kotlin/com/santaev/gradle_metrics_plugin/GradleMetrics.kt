package com.santaev.gradle_metrics_plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import com.santaev.gradle_metrics_plugin.extension.IMetricProcessorsConfigurator
import com.santaev.gradle_metrics_plugin.extension.IMetricProcessorsLoader
import com.santaev.gradle_metrics_plugin.extension.MetricProcessors
import com.santaev.gradle_metrics_plugin.utils.logger
import com.santaev.gradle_metrics_plugin.utils.whenBuildFinished
import com.santaev.gradle_metrics_plugin.utils.whenEvaluated

class GradleMetrics(
    private val project: Project,
    private val configuration: GradleMetricsPluginExtension,
    @Classpath private val pluginClasspath: ConfigurableFileCollection,
    private val metricProcessorsLoader: IMetricProcessorsLoader,
    private val metricProcessorsConfigurator: IMetricProcessorsConfigurator
) {

    private val logger = logger(this)
    private val metricsStore = MetricsStore()
    private var processors: MetricProcessors? = null

    init {
        project.whenEvaluated { initMetricProcessors() }
    }

    private fun initMetricProcessors() {
        loadProcessors()
        project.whenBuildFinished { dispatchMetrics() }
    }


    private fun loadProcessors() {
        val metricProcessorsLoadInfo = metricProcessorsLoader.load(pluginClasspath.toCollection(mutableListOf()))
        processors = metricProcessorsConfigurator.configure(
            metricProcessorsLoadInfo,
            configuration,
            metricsStore,
            project
        )
    }

    private fun dispatchMetrics() {
        processors?.dispatchers?.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }
}