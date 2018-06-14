package dslplugin

import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Project
import org.gradle.api.provider.Property

class VelocityExtension {

    final Property<Properties> properties

    void setResourceLoaderClass(Map<String, String> resourceLoaderClass) {
        resourceLoaderClass.each { entry ->
            properties.get().setProperty(entry.key, entry.value)
        }
    }

    void setResourceLoader(String resourceLoader) {
        properties.get().setProperty(
                RuntimeConstants.RESOURCE_LOADER,
                resourceLoader
        )
    }

    void setFileResourceLoaderPath(String fileResourceLoaderPath) {
        properties.get().setProperty(
                RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                fileResourceLoaderPath
        )
    }

    void setFileResourceLoaderCache(boolean fileResourceLoaderCache) {
        properties.get().setProperty(
                RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                fileResourceLoaderCache as String
        )
    }

    void setLoggerClassName(String loggerClassName) {
        properties.get().setProperty(
                RuntimeConstants.RUNTIME_LOG_NAME,
                loggerClassName
        )
    }

    VelocityExtension(Project project) {
        properties = project.objects.property(Properties)
        properties.set(new Properties())
    }
}