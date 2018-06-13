package dslplugin

import org.gradle.api.Project
import org.gradle.api.provider.Property

class VelocityExtension {
    final Property<Map<String, String>> resourceLoaderClass
    final Property<String> resourceLoader
    final Property<String> fileResourceLoaderPath
    final Property<Boolean> fileResourceLoaderCache
    final Property<String> loggerClassName

    void setResourceLoaderClass(Map<String, String> resourceLoaderClass) {
        this.resourceLoaderClass.set(resourceLoaderClass)
    }

    void setResourceLoader(String resourceLoader) {
        this.resourceLoader.set(resourceLoader)
    }

    void setFileResourceLoaderPath(String fileResourceLoaderPath) {
        this.fileResourceLoaderPath.set(fileResourceLoaderPath)
    }

    void setFileResourceLoaderCache(boolean fileResourceLoaderCache) {
        this.fileResourceLoaderCache.set(fileResourceLoaderCache)
    }

    void setLoggerClassName(String logger_class_name) {
        this.loggerClassName.set(logger_class_name)
    }

    VelocityExtension(Project project) {
        resourceLoader = project.objects.property(String)
        resourceLoaderClass = project.objects.property(Map)
        fileResourceLoaderCache = project.objects.property(Boolean)
        fileResourceLoaderPath = project.objects.property(String)
        loggerClassName = project.objects.property(String)
    }
}
