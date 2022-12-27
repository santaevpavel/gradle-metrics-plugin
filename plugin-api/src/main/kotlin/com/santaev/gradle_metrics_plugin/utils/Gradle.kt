package com.santaev.gradle_metrics_plugin.utils

import org.gradle.BuildListener
import org.gradle.BuildResult
import org.gradle.api.initialization.Settings
import org.gradle.api.invocation.Gradle

open class BuildListenerAdapter: BuildListener {
    override fun settingsEvaluated(settings: Settings) {
    }

    override fun projectsLoaded(gradle: Gradle) {
    }

    override fun projectsEvaluated(gradle: Gradle) {
    }

    @Deprecated("Deprecated in Java")
    override fun buildFinished(result: BuildResult) {
    }
}