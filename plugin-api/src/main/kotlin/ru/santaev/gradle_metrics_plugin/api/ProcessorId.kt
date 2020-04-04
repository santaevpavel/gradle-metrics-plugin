package ru.santaev.gradle_metrics_plugin.api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class ProcessorId(
    val id: String
)