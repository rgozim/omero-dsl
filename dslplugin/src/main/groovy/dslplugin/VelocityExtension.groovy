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

//class VelocityExtension {
//    final ListProperty<Map.Entry<String, String>> resourceLoaderClass
//    final Property<String> resourceLoader
//    final Property<String> fileResourceLoaderPath
//    final Property<Boolean> fileResourceLoaderCache
//    final Property<String> loggerClassName
//
//    final Property<Properties> velocityProperties
//
//
//    void setResourceLoaderClass(Map<String, String> resourceLoaderClass) {
//        this.resourceLoaderClass.set(resourceLoaderClass.entrySet())
//    }
//
//    void setResourceLoader(String resourceLoader) {
//        this.resourceLoader.set(resourceLoader)
//    }
//
//    void setFileResourceLoaderPath(String fileResourceLoaderPath) {
//        this.fileResourceLoaderPath.set(fileResourceLoaderPath)
//    }
//
//    void setFileResourceLoaderCache(boolean fileResourceLoaderCache) {
//        this.fileResourceLoaderCache.set(fileResourceLoaderCache)
//    }
//
//    void setLoggerClassName(String logger_class_name) {
//        this.loggerClassName.set(logger_class_name)
//    }
//
//    VelocityExtension(Project project) {
//        velocityProperties = project.objects.property(Properties)
//    }
//
//    /*VelocityExtension(Project project) {
//        resourceLoaderClass = project.objects.listProperty()
//        resourceLoader = project.objects.property(String)
//        fileResourceLoaderCache = project.objects.property(Boolean)
//        fileResourceLoaderPath = project.objects.property(String)
//        loggerClassName = project.objects.property(String)
//    }*/
//
//    Properties toProperties() {
//        final def props = new Properties()
//
//        if (loggerClassName.isPresent()) {
//            props.setProperty(RuntimeConstants.RUNTIME_LOG_NAME,
//                    loggerClassName.get())
//        }
//
//        if (resourceLoader.isPresent()) {
//            props.setProperty(RuntimeConstants.RESOURCE_LOADER,
//                    resourceLoader.get())
//        }
//
//        if (fileResourceLoaderPath.isPresent()) {
//            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
//                    fileResourceLoaderPath.get())
//        }
//
//        if (fileResourceLoaderCache.isPresent()) {
//            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
//                    fileResourceLoaderCache.get() as String)
//        }
//
//        if (resourceLoaderClass.isPresent()) {
//            resourceLoaderClass.get().each { entry ->
//                props.setProperty(entry.key, entry.value)
//            }
//        }
//        return props
//    }
//}