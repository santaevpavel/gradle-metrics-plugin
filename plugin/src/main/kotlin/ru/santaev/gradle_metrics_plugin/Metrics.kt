package ru.santaev.gradle_metrics_plugin


interface IMetricsStore {

    fun add(metric: Metric)
}

class MetricsStore : IMetricsStore {

    private val metrics = mutableListOf<Metric>()

    fun getMetrics(): List<Metric> = metrics

    override fun add(metric: Metric) {
        metrics.add(metric)
    }
}

data class Metric(
    val id: String,
    val value: Any,
    val unit: MetricUnit
)

sealed class MetricUnit(val value: String) {

    object None: MetricUnit("")

    object Seconds: MetricUnit("Sec")
}