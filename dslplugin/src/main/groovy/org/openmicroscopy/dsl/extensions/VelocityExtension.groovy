package org.openmicroscopy.dsl.extensions

import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Project
import org.gradle.api.provider.Property

class VelocityExtension {

    final Property<Properties> data

    void setProperty(String key, String value) {
        data.get().setProperty(key, value)
    }

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

    void maxNumberLoops(int max) {
        data.get().setProperty(
                RuntimeConstants.MAX_NUMBER_LOOPS,
                max as String
        )
    }

    void skipInvalidIterator(boolean skipInvalid) {
        data.get().setProperty(
                RuntimeConstants.SKIP_INVALID_ITERATOR,
                skipInvalid as String
        )
    }

    void checkEmptyObjects() {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void errormsgStart() {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void errormsgEnd() {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void parseDirectiveMaxdepth() {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void defineDirectiveMaxdepth() {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void provideScopeControl() {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void setEmptyCheck(boolean emptyCheck) {
        data.get().setProperty(
                RuntimeConstants.CHECK_EMPTY_OBJECTS,
                emptyCheck
        )
    }

    void setNullAllowed(boolean nullAllowed) {
        data.get().setProperty(
                RuntimeConstants.A,
                nullAllowed
        )
    }

    VelocityExtension(Project project) {
        data = project.objects.property(Properties)
        data.set(new Properties())
    }
}