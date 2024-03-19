package com.santaev.gradle_metrics_plugin

import com.santaev.gradle_metrics_plugin.extension.MetricProcessorConfigurator
import com.santaev.gradle_metrics_plugin.extension.MetricProcessorsLoader
import com.santaev.gradle_metrics_plugin.utils.logger
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.flow.FlowAction
import org.gradle.api.flow.FlowParameters
import org.gradle.api.flow.FlowProviders
import org.gradle.api.flow.FlowScope
import org.gradle.build.event.BuildEventsListenerRegistry
import javax.inject.Inject


@Suppress("UnstableApiUsage")
abstract class GradleMetricsPlugin : Plugin<Project>, com.santaev.gradle_metrics_plugin.api.Plugin {

    @Inject
    abstract override fun getBuildEventsListenerRegistry(): BuildEventsListenerRegistry

    @Inject
    abstract override fun getFlowProviders(): FlowProviders

    @Inject
    abstract override fun getFlowScope(): FlowScope

    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, GradleMetricsPluginExtension::class.java)
        val pluginClasspath = project.files()
        createConfiguration(project, pluginClasspath)

        GradleMetrics(project, extension, pluginClasspath, MetricProcessorsLoader(), MetricProcessorConfigurator(), this)
    }

    private fun createConfiguration(
        project: Project,
        pluginClasspath: ConfigurableFileCollection
    ) {
        val configuration = project.configurations.create(PLUGIN_EXTENSION_CONFIGURATION) { configuration ->
            configuration.isVisible = false
            configuration.isTransitive = true
            configuration.description = "Test"
        }
        pluginClasspath.setFrom(configuration)
    }

    companion object {
        private const val EXTENSION_NAME = "metrics"
        private const val PLUGIN_EXTENSION_CONFIGURATION = "gradleMetricsPluginExtension"
    }
}
