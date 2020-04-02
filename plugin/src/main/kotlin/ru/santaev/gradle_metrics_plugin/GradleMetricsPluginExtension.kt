package ru.santaev.gradle_metrics_plugin

import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import org.gradle.api.Action

open class GradleMetricsPluginExtension {

    val collectors = mutableListOf<CollectorConfiguration>()

    fun collectors(collectorsConfigurationAction: Action<CollectorConfigurationDsl>) {
        val collectorConfigurationDsl = CollectorConfigurationDsl()
        collectorsConfigurationAction.execute(collectorConfigurationDsl)
        collectors.addAll(collectorConfigurationDsl.collectors)
    }
}


class CollectorConfiguration(
    var id: String,
    var properties: Map<String, String>? = null
)

class CollectorConfigurationDsl: GroovyObjectSupport() {

    val collectors = mutableListOf<CollectorConfiguration>()

    @Suppress("UNCHECKED_CAST")
    override fun invokeMethod(name: String, args: Any?): Any? {
        val firstArg = (args as? Array<Any>?)?.firstOrNull()
        if (firstArg is Closure<*>) {
            val properties = mutableMapOf<String, String>()
            firstArg.delegate = properties
            firstArg.call()
            addCollector(name, properties)
        } else {
            addCollector(name)
        }
        return null
    }

    override fun getProperty(property: String): Any {
        addCollector(property)
        return property
    }

    private fun addCollector(name: String, properties: Map<String, String>? = null) {
        collectors.add(CollectorConfiguration(name, properties))
    }
}