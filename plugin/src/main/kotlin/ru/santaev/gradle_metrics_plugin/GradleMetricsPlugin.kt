package ru.santaev.gradle_metrics_plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("UnstableApiUsage")
class GradleMetricsPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("Applying plugin")
        project.task("GradleMetricsPlugin") { task ->
            task.doLast {
                println("Hello from the GradleMetricsPlugin")
            }
        }
    }
}