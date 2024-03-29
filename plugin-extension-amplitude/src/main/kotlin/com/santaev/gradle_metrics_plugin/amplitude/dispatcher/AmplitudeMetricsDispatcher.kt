package com.santaev.gradle_metrics_plugin.amplitude.dispatcher

import com.amplitude.Amplitude
import com.amplitude.AmplitudeLog
import com.amplitude.Event
import org.gradle.api.Project
import org.json.JSONObject
import com.santaev.gradle_metrics_plugin.api.Config
import com.santaev.gradle_metrics_plugin.api.Metric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.Plugin
import com.santaev.gradle_metrics_plugin.api.dispatcher.BaseMetricDispatcher
import com.santaev.gradle_metrics_plugin.utils.logger
import java.util.*


@MetricProcessorId("AmplitudeDispatcher")
class AmplitudeMetricsDispatcher : BaseMetricDispatcher() {

    private val logger = logger(this)
    private lateinit var amplitude: Amplitude
    private val apiKey by lazy { config.properties["apiKey"].orEmpty() }
    private val userId: String
        get() = UUID.nameUUIDFromBytes(project.rootDir.absolutePath.toByteArray()).toString()

    override fun init(config: Config, project: Project, plugin: Plugin) {
        super.init(config, project, plugin)
        initAmplitude()
        setupLogger()
    }

    override fun dispatch(metrics: List<Metric>) {
        val event = Event(BUILD_FINISHED_EVENT_NAME, userId)
        event.eventProperties = getEventProperties(metrics)
        amplitude.logEvent(event)
        amplitude.flushEvents()
        Thread.sleep(DISPATCHING_DELAY_MILLIS)
    }

    private fun getEventProperties(metrics: List<Metric>): JSONObject {
        val properties = JSONObject()
        metrics.forEach { metric ->
            properties.put(metric.id, metric.value)
        }
        return properties
    }

    private fun initAmplitude() {
        amplitude = Amplitude.getInstance()
        amplitude.init(apiKey)
        amplitude.setLogMode(AmplitudeLog.LogMode.DEBUG)
        logger.info("ApiKey: $apiKey")
    }

    private fun setupLogger() {
        amplitude.setLogger(object : AmplitudeLog() {
            override fun log(tag: String, message: String, messageMode: LogMode) {
                logger.info("$tag $message")
            }
        })
    }

    companion object {
        private const val BUILD_FINISHED_EVENT_NAME = "BuildFinished"
        private const val DISPATCHING_DELAY_MILLIS = 2000L
    }
}