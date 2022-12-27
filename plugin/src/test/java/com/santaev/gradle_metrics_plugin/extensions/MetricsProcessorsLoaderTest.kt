package com.santaev.gradle_metrics_plugin.extensions

import com.santaev.gradle_metrics_plugin.api.*
import com.santaev.gradle_metrics_plugin.extension.ExtensionsProviderJarLoader
import com.santaev.gradle_metrics_plugin.extension.MetricProcessorsLoader
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should be`
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test
import com.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import com.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk


class MetricsProcessorsLoaderTest {

    @MockK
    private lateinit var processorsLoader: MetricProcessorsLoader

    @MockK
    private lateinit var extensionsProviderJarLoader: ExtensionsProviderJarLoader

    @Before
    fun setup() {
        MockKAnnotations.init(this)
        processorsLoader = createMetricsProcessorsLoader()
    }

    @Test
    fun `should load all collectors from extension providers if all collectors has annotations`() {
        // Arrange
        every { extensionsProviderJarLoader.load(any()) } returns EXTENSION_LOADERS
        processorsLoader = createMetricsProcessorsLoader()
        // Act
        val loadedCollectors = processorsLoader.load(emptyList()).collectors
        // Assert
        loadedCollectors["Metric1"]?.instantiator?.instantiate() `should be instance of` MetricCollector1::class.java
        loadedCollectors["Metric2"]?.instantiator?.instantiate() `should be instance of` MetricCollector2::class.java
        loadedCollectors["Metric3"]?.instantiator?.instantiate() `should be instance of` MetricCollector3::class.java
        loadedCollectors.size `should be` 3
    }

    @Test
    fun `should load annotated collectors from extension providers`() {
        // Arrange
        val extensionProvider = mockk<IExtensionsProvider> {
            every { provideCollectors() } returns listOf(
                MetricCollectorWithoutAnnotation::class.java,
                MetricCollector1::class.java,
                MetricCollector2::class.java
            )
            every { provideDispatcher() } returns emptyList()
        }
        every { extensionsProviderJarLoader.load(any()) } returns listOf(extensionProvider)

        processorsLoader = createMetricsProcessorsLoader()
        // Act
        val loadedCollectors = processorsLoader.load(emptyList()).collectors
        // Assert
        loadedCollectors["Metric1"]?.instantiator?.instantiate() `should be instance of` MetricCollector1::class.java
        loadedCollectors["Metric2"]?.instantiator?.instantiate() `should be instance of` MetricCollector2::class.java
        loadedCollectors.size `should be` 2
    }

    @Test
    fun `should load annotated dispatchers from extension providers`() {
        // Arrange
        val extensionProvider = mockk<IExtensionsProvider> {
            every { provideDispatcher() } returns listOf(
                MetricDispatcherWithoutAnnotation::class.java,
                MetricDispatcher1::class.java
            )
            every { provideCollectors() } returns emptyList()
        }
        every { extensionsProviderJarLoader.load(any()) } returns listOf(extensionProvider)
        processorsLoader = createMetricsProcessorsLoader()
        // Act
        val loadedDispatchers = processorsLoader.load(emptyList()).dispatchers
        // Assert
        loadedDispatchers["Dispatcher1"]?.instantiator?.instantiate() `should be instance of` MetricDispatcher1::class.java
        loadedDispatchers.size `should be` 1
    }

    private fun createMetricsProcessorsLoader(): MetricProcessorsLoader {
        return MetricProcessorsLoader(extensionsProviderJarLoader)
    }

    @MetricProcessorId("Metric1")
    class MetricCollector1 : IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    @MetricProcessorId("Metric2")
    class MetricCollector2 : IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    @MetricProcessorId("Metric3")
    class MetricCollector3 : IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    class MetricCollectorWithoutAnnotation : IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    @MetricProcessorId("Dispatcher1")
    class MetricDispatcher1 : IMetricsDispatcher {
        override fun init(config: Config, project: Project) {
        }

        override fun dispatch(metrics: List<Metric>) {
        }
    }

    private class MetricDispatcherWithoutAnnotation : IMetricsDispatcher {
        override fun init(config: Config, project: Project) {
        }

        override fun dispatch(metrics: List<Metric>) {
        }
    }

    companion object {
        private val EXTENSION_PROVIDER_1 = mockk<IExtensionsProvider> {
            every { provideCollectors() } returns listOf(MetricCollector1::class.java)
            every { provideDispatcher() } returns listOf(MetricDispatcher1::class.java)
        }
        private val EXTENSION_PROVIDER_2 = mockk<IExtensionsProvider> {
            every { provideCollectors() } returns listOf(
                MetricCollector2::class.java,
                MetricCollector3::class.java
            )
            every { provideDispatcher() } returns emptyList()
        }
        private val EXTENSION_LOADERS = listOf(EXTENSION_PROVIDER_1, EXTENSION_PROVIDER_2)
    }
}