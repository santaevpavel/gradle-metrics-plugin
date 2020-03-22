package ru.santaev.gradle_metrics_plugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.tasks.Classpath
import ru.santaev.gradle_metrics_plugin.extension.IMetricsProcessorsLoader
import ru.santaev.gradle_metrics_plugin.extension.MetricProcessors
import ru.santaev.gradle_metrics_plugin.utils.logger
import ru.santaev.gradle_metrics_plugin.utils.whenBuildFinished
import ru.santaev.gradle_metrics_plugin.utils.whenEvaluated

class GradleMetrics(
    private val project: Project,
    private val extension: GradleMetricsPluginExtension,
    @Classpath private val pluginClasspath: ConfigurableFileCollection,
    private val metricsProcessorsLoader: IMetricsProcessorsLoader
) {

    private val logger = logger(this)
    private val metricsStore = MetricsStore()
    private var processors: MetricProcessors? = null

    init {
        project.whenEvaluated { initMetricProcessors() }
    }


    private fun initMetricProcessors() {
        logger.info("Loading metrics processors")
        processors = metricsProcessorsLoader.load(pluginClasspath.toCollection(mutableListOf()))
        processors?.collectors?.forEach { collector ->
            collector.init(metricsStore, project)
        }
        logCollectorsAndDispatchers()
        project.whenBuildFinished { dispatchMetrics() }
    }


    private fun dispatchMetrics() {
        processors?.dispatchers?.forEach { dispatcher ->
            dispatcher.dispatch(metricsStore.getMetrics())
        }
    }

    private fun logCollectorsAndDispatchers() {
        println(
            "Configured metric collectors   " +
                    extension.collectorsCreationDsl.collectorConfigurations.joinToString(", ")
        )
        val log = buildString {
            appendln("Metric collectors:")
            processors?.collectors?.forEach { collector ->
                appendln("\t- ${collector::class.java.simpleName}")
            }
            appendln("Metric dispatchers:")
            processors?.dispatchers?.forEach { dispatcher ->
                appendln("\t- ${dispatcher::class.java.simpleName}")
            }
        }
        logger.info(log)
        println(log)
    }

}