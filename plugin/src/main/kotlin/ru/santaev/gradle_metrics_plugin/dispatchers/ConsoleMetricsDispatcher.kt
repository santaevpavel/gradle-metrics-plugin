package ru.santaev.gradle_metrics_plugin.dispatchers

import ru.santaev.gradle_metrics_plugin.Metric

class ConsoleMetricsDispatcher : IMetricsDispatcher {

    override fun dispatch(metrics: List<Metric>) {
        val output = buildString {
            appendln(">>>>>>>>>> Metrics output <<<<<<<<<")
            metrics.forEach { metric ->
                append("\t").append(metric.id)
                append("\t\t\t").append(metric.value).appendln(" ${metric.unit.name}")
            }
            appendln()
            appendln(">>>>>>>>>> Metrics output <<<<<<<<<")
        }
        println(output)
    }
}