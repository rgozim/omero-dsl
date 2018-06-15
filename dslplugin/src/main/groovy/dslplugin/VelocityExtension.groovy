package dslplugin

import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Project
import org.gradle.api.provider.Property

class VelocityExtension {

    final Property<Properties> props

    void setResourceLoaderClass(Map<String, String> resourceLoaderClass) {
        resourceLoaderClass.each { entry ->
            props.get().setProperty(entry.key, entry.value)
        }
    }

    void setResourceLoader(String resourceLoader) {
        props.get().setProperty(
                RuntimeConstants.RESOURCE_LOADER,
                resourceLoader
        )
    }

    void setFileResourceLoaderPath(String fileResourceLoaderPath) {
        props.get().setProperty(
                RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                fileResourceLoaderPath
        )
    }

    void setFileResourceLoaderCache(boolean fileResourceLoaderCache) {
        props.get().setProperty(
                RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                fileResourceLoaderCache as String
        )
    }

    void setLoggerClassName(String loggerClassName) {
        props.get().setProperty(
                RuntimeConstants.RUNTIME_LOG_NAME,
                loggerClassName
        )
    }

    VelocityExtension(Project project) {
        props = project.objects.property(Properties)
        props.set(new Properties())
    }
}