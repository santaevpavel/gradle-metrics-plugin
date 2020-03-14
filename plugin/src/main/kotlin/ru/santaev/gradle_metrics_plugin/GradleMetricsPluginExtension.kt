package ru.santaev.gradle_metrics_plugin

import org.gradle.api.Action

open class GradleMetricsPluginExtension {

    val collectorsCreationDsl = CollectorsConfigurationDsl()

    fun collectors(collectorsConfigurationAction: Action<CollectorsConfigurationDsl>) {
        collectorsConfigurationAction.execute(collectorsCreationDsl)
    }
}

class CollectorsConfigurationDsl {

    val collectorConfigurations = mutableListOf<CollectorConfiguration>()

    @JvmOverloads
    fun id(id: String, collectorConfigurationAction: Action<CollectorConfigurationDsl>? = null) {
        val collectorConfigurationDsl = CollectorConfigurationDsl()
        collectorConfigurationAction?.execute(collectorConfigurationDsl)
        collectorConfigurations.add(CollectorConfiguration(id, collectorConfigurationDsl))
    }

    data class CollectorConfiguration(
        val id: String,
        val collectorConfigurationDsl: CollectorConfigurationDsl
    )
}

class CollectorConfigurationDsl {

}