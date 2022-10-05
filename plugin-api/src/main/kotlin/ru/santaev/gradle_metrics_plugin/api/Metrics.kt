package ru.santaev.gradle_metrics_plugin.api


interface IMetricsStore {

    fun add(metric: Metric)
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

    object Kilobytes : MetricUnit("kilobytes")

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

fun StringMetric(
    id: String,
    value: String,
    unit: MetricUnit
) : Metric {
    return Metric(id, value, unit)
}