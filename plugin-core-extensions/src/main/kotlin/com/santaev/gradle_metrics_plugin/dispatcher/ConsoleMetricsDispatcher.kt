package com.santaev.gradle_metrics_plugin.dispatcher

import org.gradle.api.Project
import org.gradle.api.logging.Logger
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.Metric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.Plugin
import com.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

@MetricProcessorId("ConsoleDispatcher")
class ConsoleMetricsDispatcher : IMetricsDispatcher {

    private lateinit var logger: Logger

    override fun init(config: Config, project: Project, plugin: Plugin) {
        logger = project.logger
    }

    override fun dispatch(metrics: List<Metric>) {
        val output = buildString {
            appendln("\t-----------------------------------------------")
            appendln("\t--------------- Metrics output ----------------")
            appendln(String.format("\t%-25s %-10s %s", "-------------------------", "----------", "----------"))
            appendln(String.format("\t%-25s %-10s %s", "Metric id", "Value", "Unit"))
            appendln(String.format("\t%-25s %-10s %s", "-------------------------", "----------", "----------"))
            metrics.forEach { metric ->
                appendln(String.format("\t%-25s %-10s %s", metric.id, metric.value, metric.unit.name))
            }
            appendln(String.format("\t%-25s %-10s %s", "-------------------------", "----------", "----------"))
            appendln()
        }
        logger.lifecycle(output)
    }
}