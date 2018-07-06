package extensions

import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Project
import org.gradle.api.provider.Property

class VelocityExtension {

    final Property<Properties> data

    void setResourceLoaderClass(Map<String, String> resourceLoaderClass) {
        resourceLoaderClass.each { entry ->
            data.get().setProperty(entry.key, entry.value)
        }
    }

    void setResourceLoader(String resourceLoader) {
        data.get().setProperty(
                RuntimeConstants.RESOURCE_LOADER,
                resourceLoader
        )
    }

    void setFileResourceLoaderPath(String fileResourceLoaderPath) {
        data.get().setProperty(
                RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                fileResourceLoaderPath
        )
    }

    void setFileResourceLoaderCache(boolean fileResourceLoaderCache) {
        data.get().setProperty(
                RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                fileResourceLoaderCache as String
        )
    }

    void setLoggerClassName(String loggerClassName) {
        data.get().setProperty(
                RuntimeConstants.RUNTIME_LOG_NAME,
                loggerClassName
        )
    }

    VelocityExtension(Project project) {
        data = project.objects.property(Properties)
        data.set(new Properties())
    }
}