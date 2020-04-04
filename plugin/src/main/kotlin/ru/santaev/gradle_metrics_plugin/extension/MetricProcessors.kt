package ru.santaev.gradle_metrics_plugin.extension

import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher

data class MetricProcessors(
    val collectors: List<IMetricsCollector>,
    val dispatchers: List<IMetricsDispatcher>
)


data class MetricProcessorsLoadInfo(
    val collectors: Map<String, MetricCollectorLoadInfo>,
    val dispatchers: Map<String, MetricDispatcherLoadInfo>
)

class MetricCollectorLoadInfo(
    val collectorId: String,
    val instantiator: Instantiator<IMetricsCollector>
)

class MetricDispatcherLoadInfo(
    val collectorId: String,
    val instantiator: Instantiator<IMetricsDispatcher>
)


interface Instantiator<T> {

    fun instantiate(): T
}

class InstantiatorImpl<T>(private val clazz: Class<out T>) : Instantiator<T> {

    override fun instantiate(): T {
        return clazz.newInstance()
    }
}