package ru.santaev.gradle_metrics_plugin.dispatcher

import ru.santaev.gradle_metrics_plugin.api.Metric
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

class ConsoleMetricsDispatcher : IMetricsDispatcher {

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
        println(output)
    }
}