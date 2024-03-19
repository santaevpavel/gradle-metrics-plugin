package com.santaev.gradle_metrics_plugin.dispatcher

import com.google.gson.GsonBuilder
import org.gradle.api.Project
import org.gradle.api.logging.LogLevel
import org.gradle.api.logging.Logger
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.Metric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.Plugin
import com.santaev.gradle_metrics_plugin.api.dispatcher.BaseMetricDispatcher
import java.io.File

@MetricProcessorId("JsonFileDispatcher")
class JsonFileMetricsDispatcher : BaseMetricDispatcher() {

    private lateinit var logger: Logger
    private lateinit var outputFile: File
    private val gson = GsonBuilder()
        .setPrettyPrinting()
        .create()

    override fun init(config: Config, project: Project, plugin: Plugin) {
        super.init(config, project, plugin)
        logger = project.logger
    }

    override fun dispatch(metrics: List<Metric>) {
        if (!createOutputFile(config.properties["path"])) return
        runCatching {
            outputFile.appendText(gson.toJson(metrics))
        }.onFailure { err ->
            if (project.containsCleanTask()) {
                logger.log(
                    LogLevel.WARN,
                    "Unable to save metrics to file ${outputFile.absoluteFile}",
                    err
                )
            }
        }
    }

    private fun Project.containsCleanTask(): Boolean {
        return gradle.startParameter
            .taskNames
            .map { it.substringAfterLast(":", it) }
            .contains("clean")
    }

    private fun createOutputFile(path: String?): Boolean {
        return runCatching {
            outputFile = if (path != null) {
                File(path)
            } else {
                File(project.buildDir, "output/metrics.txt")
            }
            outputFile.parentFile.mkdirs()
            outputFile.createNewFile()
        }
            .onFailure { err ->
                logger.log(
                    LogLevel.INFO,
                    "Unable to create output file ${outputFile.absoluteFile}",
                    err
                )
            }
            .getOrElse { false }
    }
}