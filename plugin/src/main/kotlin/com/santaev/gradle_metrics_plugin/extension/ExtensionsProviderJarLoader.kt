package com.santaev.gradle_metrics_plugin.extension

import com.santaev.gradle_metrics_plugin.api.IExtensionsProvider
import com.santaev.gradle_metrics_plugin.utils.logger
import java.io.Closeable
import java.io.File
import java.io.IOException
import java.net.URL
import java.net.URLClassLoader
import java.util.*


class ExtensionsProviderJarLoader : Closeable {

    private val logger = logger(this)
    private val classLoadersClosable = mutableListOf<Closeable>()

    fun load(jarFiles: List<File>): List<IExtensionsProvider> {
        return jarFiles
            .mapNotNull { loadJar(it) }
            .mapNotNull { loadExtensionProvider(it) }
    }

    override fun close() {
        classLoadersClosable.forEach { it.close() }
    }

    private fun loadJar(file: File): Class<*>? {
        val urls = arrayOf(URL("jar:file:" + file.absolutePath + "!/"))
        val classLoader = URLClassLoader.newInstance(urls, this::class.java.classLoader)
        try {
            val extensionPropertiesFile = classLoader.getResource(EXTENSION_PROPERTY_FILE_PATH)
            if (extensionPropertiesFile == null) {
                logger.error("Properties file not found in jar file (${file.name})")
                return null
            }
            val extensionProperties = loadProperties(extensionPropertiesFile)
            val extensionProviderName = extensionProperties.getProperty(
                EXTENSION_PROVIDER_PROPERTY_NAME
            )
            if (extensionProviderName == null) {
                logger.error("Not found extension provider class name property")
                return null
            }
            return classLoader.loadClass(extensionProviderName)
        } finally {
            classLoadersClosable.add(classLoader)
        }
    }

    private fun loadExtensionProvider(clazz: Class<*>): IExtensionsProvider? {
        return try {
            clazz.newInstance() as IExtensionsProvider
        } catch (err: ClassCastException) {
            logger.error(
                "Provided extensions provider (${clazz.name}) in properties should implement IExtensionsProvider",
                err
            )
            null
        } catch (err: Throwable) {
            logger.error("Error while instantiate extension provider. Class is $clazz", err)
            null
        }
    }

    @Throws(IOException::class)
    private fun loadProperties(fromUrl: URL): Properties {
        return Properties().apply {
            fromUrl.openStream().use { stream ->
                load(stream)
            }
        }
    }

    companion object {
        private const val EXTENSION_META_INFO_DIR_NAME = "gradle-metrics-plugin-extension"
        private const val EXTENSION_PROPERTY_FILE_NAME = "extension.properties"
        private const val META_INF_DIR = "META-INF"
        private const val EXTENSION_PROVIDER_PROPERTY_NAME = "extension-provider"
        private const val EXTENSION_PROPERTY_FILE_PATH =
            "$META_INF_DIR/$EXTENSION_META_INFO_DIR_NAME/$EXTENSION_PROPERTY_FILE_NAME"
    }
}