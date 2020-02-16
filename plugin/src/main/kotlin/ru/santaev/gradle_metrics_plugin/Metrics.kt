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

sealed class MetricUnit(val name: String) {

    object None : MetricUnit("")

    object Seconds : MetricUnit("seconds")

    object Pieces : MetricUnit("pieces")

    class Custom(name: String) : MetricUnit(name)

}

fun LongMetric(
    id: String,
    value: Long,
    unit: MetricUnit
) : Metric {
    return Metric(id, value, unit)
}

fun DoubleMetric(
    id: String,
    value: Double,
    unit: MetricUnit
) : Metric {
    return Metric(id, value, unit)
}