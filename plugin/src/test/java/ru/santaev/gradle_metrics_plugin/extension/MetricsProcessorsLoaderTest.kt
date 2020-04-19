package ru.santaev.gradle_metrics_plugin.extension

import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.stub
import org.amshove.kluent.`should be instance of`
import org.amshove.kluent.`should be`
import org.gradle.api.Project
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import ru.santaev.gradle_metrics_plugin.api.*
import ru.santaev.gradle_metrics_plugin.api.collector.IMetricsCollector
import ru.santaev.gradle_metrics_plugin.api.dispatcher.IMetricsDispatcher


@RunWith(MockitoJUnitRunner::class)
class MetricsProcessorsLoaderTest {

    @Mock
    private lateinit var processorsLoader: MetricProcessorsLoader
    @Mock
    private lateinit var extensionsProviderJarLoader: ExtensionsProviderJarLoader

    @Before
    fun setup() {
        processorsLoader = createMetricsProcessorsLoader()
    }

    @Test
    fun `should load all collectors from extension providers if all collectors has annotations`() {
        // Arrange
        extensionsProviderJarLoader.stub {
            on { load(any()) } doReturn EXTENSION_LOADERS
        }
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
        val extensionProvider = mock<IExtensionsProvider> {
            on { provideCollectors() } doReturn listOf(
                MetricCollectorWithoutAnnotation::class.java,
                MetricCollector1::class.java,
                MetricCollector2::class.java
            )
        }
        extensionsProviderJarLoader.stub {
            on { load(any()) } doReturn listOf(extensionProvider)
        }
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
        val extensionProvider = mock<IExtensionsProvider> {
            on { provideDispatcher() } doReturn listOf(
                MetricDispatcherWithoutAnnotation::class.java,
                MetricDispatcher1::class.java
            )
        }
        extensionsProviderJarLoader.stub {
            on { load(any()) } doReturn listOf(extensionProvider)
        }
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
    private class MetricCollector1: IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    @MetricProcessorId("Metric2")
    private class MetricCollector2: IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    @MetricProcessorId("Metric3")
    private class MetricCollector3: IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    private class MetricCollectorWithoutAnnotation: IMetricsCollector {
        override fun init(config: Config, metricsStore: IMetricsStore, project: Project) {
        }
    }

    @MetricProcessorId("Dispatcher1")
    private class MetricDispatcher1: IMetricsDispatcher {
        override fun init(config: Config, project: Project) {
        }

        override fun dispatch(metrics: List<Metric>) {
        }
    }

    private class MetricDispatcherWithoutAnnotation: IMetricsDispatcher {
        override fun init(config: Config, project: Project) {
        }

        override fun dispatch(metrics: List<Metric>) {
        }
    }

    companion object {
        private val EXTENSION_PROVIDER_1 = mock<IExtensionsProvider> {
            on { provideCollectors() } doReturn listOf(MetricCollector1::class.java)
            on { provideDispatcher() } doReturn listOf(MetricDispatcher1::class.java)
        }
        private val EXTENSION_PROVIDER_2 = mock<IExtensionsProvider> {
            on { provideCollectors() } doReturn listOf(MetricCollector2::class.java, MetricCollector3::class.java)
        }
        private val EXTENSION_LOADERS = listOf(EXTENSION_PROVIDER_1, EXTENSION_PROVIDER_2)
    }
}