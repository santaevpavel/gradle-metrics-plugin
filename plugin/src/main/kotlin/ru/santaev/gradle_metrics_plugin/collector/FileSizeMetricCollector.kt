package ru.santaev.gradle_metrics_plugin.collector

import org.gradle.api.Project
import ru.santaev.gradle_metrics_plugin.api.LongMetric
import ru.santaev.gradle_metrics_plugin.api.MetricUnit
import ru.santaev.gradle_metrics_plugin.api.collector.BaseMetricCollector
import ru.santaev.gradle_metrics_plugin.utils.sizeOnKilobytes
import java.io.File

open class FileSizeMetricCollector(
    private val metricId: String,
    private val fileResolver: FileResolver,
    private val isPublishWhenNoFile: Boolean
) : BaseMetricCollector() {

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
            fun fromPath(path: String): FileResolver = object : FileResolver {
                override fun getFile(project: Project): File = File(path)
            }
        }
    }
}

class JarFileSizeMetricCollector : FileSizeMetricCollector(
    metricId = "JarFileSize",
    fileResolver = JarFileResolver(),
    isPublishWhenNoFile = false
) {

    class JarFileResolver : FileResolver {

        override fun getFile(project: Project): File? {
            return File(project.buildDir.absolutePath + "/libs/")
                .takeIf { it.exists() && it.isDirectory }
                ?.listFiles { _, name -> name.endsWith(".jar") }
                ?.first()
        }
    }
}