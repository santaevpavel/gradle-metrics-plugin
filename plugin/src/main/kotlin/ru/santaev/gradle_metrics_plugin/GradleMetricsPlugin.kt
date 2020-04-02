package ru.santaev.gradle_metrics_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import ru.santaev.gradle_metrics_plugin.extension.MetricsProcessorsLoader


@Suppress("UnstableApiUsage")
class GradleMetricsPlugin : Plugin<Project> {

    @Suppress("MoveLambdaOutsideParentheses")
    override fun apply(project: Project) {
        val extension = project.extensions.create(EXTENSION_NAME, GradleMetricsPluginExtension::class.java)
        val pluginClasspath = project.files()
        createConfiguration(project, pluginClasspath)
        GradleMetrics(project, extension, pluginClasspath, MetricsProcessorsLoader())
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
