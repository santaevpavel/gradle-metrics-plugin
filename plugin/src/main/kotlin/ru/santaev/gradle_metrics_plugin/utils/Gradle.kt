package ru.santaev.gradle_metrics_plugin.utils

import org.gradle.BuildResult
import org.gradle.api.Project
import org.gradle.api.invocation.Gradle

fun Project.whenBuildFinished(block: () -> Unit) {
    gradle.addBuildListener(object : BuildListenerAdapter() {
        override fun buildFinished(result: BuildResult) {
            block()
        }
    })
}

fun Project.whenEvaluated(block: () -> Unit) {
    gradle.addBuildListener(object : BuildListenerAdapter() {
        override fun projectsEvaluated(gradle: Gradle) {
            block()
        }
    })
}