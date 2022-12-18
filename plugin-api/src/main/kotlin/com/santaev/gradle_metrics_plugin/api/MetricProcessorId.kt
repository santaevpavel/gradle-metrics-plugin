package com.santaev.gradle_metrics_plugin.api

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class MetricProcessorId(
    val id: String
)