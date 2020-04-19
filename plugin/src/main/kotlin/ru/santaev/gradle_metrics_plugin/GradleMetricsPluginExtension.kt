package ru.santaev.gradle_metrics_plugin

import groovy.lang.Closure
import groovy.lang.GroovyObjectSupport
import org.gradle.api.Action

open class GradleMetricsPluginExtension {

    val collectors = mutableListOf<ProcessorConfiguration>()
    val dispatchers = mutableListOf<ProcessorConfiguration>()

    fun collectors(collectorsConfigurationAction: Action<ProcessorConfigurationDsl>) {
        val processorConfigurationDsl = ProcessorConfigurationDsl()
        collectorsConfigurationAction.execute(processorConfigurationDsl)
        collectors.addAll(processorConfigurationDsl.processors)
    }

    fun dispatchers(dispatchersConfigurationAction: Action<ProcessorConfigurationDsl>) {
        val processorConfigurationDsl = ProcessorConfigurationDsl()
        dispatchersConfigurationAction.execute(processorConfigurationDsl)
        dispatchers.addAll(processorConfigurationDsl.processors)
    }
}


class ProcessorConfiguration(
    var id: String,
    var properties: Map<String, String>? = null
)

class ProcessorConfigurationDsl: GroovyObjectSupport() {

    val processors = mutableListOf<ProcessorConfiguration>()

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
        processors.add(ProcessorConfiguration(name, properties))
    }
}