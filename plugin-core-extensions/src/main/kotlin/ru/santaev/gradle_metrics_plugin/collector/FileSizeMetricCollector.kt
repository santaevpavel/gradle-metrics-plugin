package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.LongMetric
import ru.santaev.gradle_metrics_plugin.api.MetricProcessorId
import ru.santaev.gradle_metrics_plugin.api.MetricUnit
import ru.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import ru.santaev.gradle_metrics_plugin.utils.sizeOnKilobytes
import java.io.File

abstract class FileSizeMetricCollector(
    private val isPublishWhenNoFile: Boolean
) : BaseMetricCollector() {

    protected abstract val fileResolver: FileResolver
    protected abstract val metricId: String

    override fun onBuildFinish() {
        val project = project ?: return
        val size = fileResolver.getFile(project)?.takeIf { it.exists() && it.isFile }?.sizeOnKilobytes

        if (size != null) {
            metricsStore?.add(LongMetric(metricId, size, MetricUnit.Kilobytes))
        } else if (isPublishWhenNoFile) {
            metricsStore?.add(LongMetric(metricId, 0, MetricUnit.Kilobytes))
        }
    }

    interface FileResolver {

        fun getFile(project: Project): File?

        companion object {
            fun fromPath(path: String): FileResolver = object :
                FileResolver {
                override fun getFile(project: Project): File = File(path)
            }
        }
    }
}

class JarFileSizeMetricCollector : FileSizeMetricCollector(
    isPublishWhenNoFile = false
) {

    override val metricId: String = "JarFileSize"
    override val fileResolver: FileResolver = JarFileResolver()

    class JarFileResolver : FileResolver {

        override fun getFile(project: Project): File? {
            return File(project.buildDir.absolutePath + "/libs/")
                .takeIf { it.exists() && it.isDirectory }
                ?.listFiles { _, name -> name.endsWith(".jar") }
                ?.first()
        }
    }
}

@MetricProcessorId("FileSize")
class ConfigurableFileSizeMetricCollector : FileSizeMetricCollector(
    isPublishWhenNoFile = false
) {

    override val metricId: String by lazy { config?.properties?.get("metricId").orEmpty() }
    override val fileResolver: FileResolver = ConfigFileResolver()

    private inner class ConfigFileResolver: FileResolver {

        override fun getFile(project: Project): File? {
            val path = config?.properties?.get("path") ?: return null
            return File(path).takeIf { it.exists() }
        }
    }
}