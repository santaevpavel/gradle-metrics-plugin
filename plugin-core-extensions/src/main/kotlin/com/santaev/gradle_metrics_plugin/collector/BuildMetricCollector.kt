package com.santaev.gradle_metrics_plugin.collector

import com.santaev.gradle_metrics_plugin.api.DoubleMetric
import com.santaev.gradle_metrics_plugin.api.MetricProcessorId
import com.santaev.gradle_metrics_plugin.api.MetricUnit
import com.santaev.gradle_metrics_plugin.api.StringMetric
import com.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import com.santaev.gradle_metrics_plugin.utils.logger
import org.gradle.BuildResult
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskState
import org.gradle.build.event.BuildEventsListenerRegistry
import org.gradle.internal.operations.BuildOperationDescriptor
import org.gradle.internal.operations.BuildOperationListener
import org.gradle.internal.operations.OperationFinishEvent
import org.gradle.tooling.events.FinishEvent
import org.gradle.tooling.events.OperationCompletionListener
import org.gradle.tooling.events.lifecycle.BuildPhaseFinishEvent
import org.gradle.tooling.events.task.TaskFinishEvent
import javax.inject.Inject


@MetricProcessorId("Build")
class BuildMetricCollector: BaseMetricCollector() {

    private val logger = logger(this)

    init {
        val buildTimeCalculatorListener = BuildTimeCalculatorListener { buildTimeMillis ->
            collectBuildTime(buildTimeMillis)
        }
        afterInit {
            val tasksEventsService = project.provider { TaskEventsService(buildTimeCalculatorListener) }
            plugin.getBuildEventsListenerRegistry().onTaskCompletion(tasksEventsService)
        }
        afterBuild { result ->
            collectStatus(result)
            buildTimeCalculatorListener.buildFinished()
        }
    }

    private fun collectBuildTime(buildTimeMillis: Long) {
        metricsStore?.add(
            DoubleMetric(
                id = BUILD_TIME_METRIC_ID,
                value = buildTimeMillis / 1000.0,
                unit = MetricUnit.Seconds
            )
        )
    }

    private fun collectStatus(result: BuildResult) {
        metricsStore?.add(
            StringMetric(
                id = BUILD_STATUS_METRIC_ID,
                value = if (result.failure == null) "Successful" else "Failed",
                unit = MetricUnit.None
            )
        )
    }

    companion object {
        private const val BUILD_TIME_METRIC_ID = "BuildTime"
        private const val BUILD_STATUS_METRIC_ID = "BuildStatus"
    }


    class TaskEventsService(private val listener: OperationCompletionListener):
        OperationCompletionListener {

        override fun onFinish(event: FinishEvent) {
            if (event is TaskFinishEvent) {
                listener.onFinish(event)
            }
        }
    }
}


private class BuildTimeCalculatorListener(
    private val onBuildFinished: (buildTimeMillis: Long) -> Unit
): OperationCompletionListener {

    private var buildStartTime = System.currentTimeMillis()
    private var isBuildStarted = false

    fun buildFinished() {
        val buildTime = System.currentTimeMillis() - buildStartTime
        onBuildFinished(buildTime)
    }

    override fun onFinish(event: FinishEvent) {
        if (!isBuildStarted) {
            isBuildStarted = true
        }
    }
}