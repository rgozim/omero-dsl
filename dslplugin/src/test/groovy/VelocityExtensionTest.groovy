import dslplugin.VelocityExtension
import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.FileResourceLoader
import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import spock.lang.Specification

class VelocityExtensionTest extends Specification {

    Project project

    private static final String RESOURCE_LOADER = "file"
    private static final String RUNTIME_LOG_NAME = "logger"
    private static final String FILE_RESOURCE_LOADER_PATH = "src/main"
    private static final boolean FILE_RESOURCE_LOADER_CACHE = true
    private static final Map<String, String> RESOURCE_LOADER_CLASS =
            ["file.resource.loader.class": FileResourceLoader.class.getName()]

    def setup() {
        project = ProjectBuilder.builder().build();
    }

    def "can create VelocityExtension"() {
        when:
        def extension = createDefault(project)

        then:
        extension.resourceLoader.get() == RESOURCE_LOADER
        extension.resourceLoaderClass.get() == RESOURCE_LOADER_CLASS
        extension.loggerClassName.get() == RUNTIME_LOG_NAME
    }

    def "can convert VelocityExtension to Properties"() {
        when:
        def extension = createDefault(project)
        def properties = extension.toProperties()

        then:
        properties != null
        properties.get(RuntimeConstants.RUNTIME_LOG_NAME) == RUNTIME_LOG_NAME
        properties.get(RuntimeConstants.RESOURCE_LOADER) == RESOURCE_LOADER
        properties.get(RuntimeConstants.FILE_RESOURCE_LOADER_PATH) == FILE_RESOURCE_LOADER_PATH
        properties.get(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE) == String.valueOf(FILE_RESOURCE_LOADER_CACHE)
    }

    static VelocityExtension createDefault(Project project) {
        def extension = new VelocityExtension(project)
        extension.resourceLoader = RESOURCE_LOADER
        extension.fileResourceLoaderPath = FILE_RESOURCE_LOADER_PATH
        extension.fileResourceLoaderCache = FILE_RESOURCE_LOADER_CACHE
        extension.loggerClassName = RUNTIME_LOG_NAME
        extension.resourceLoaderClass = RESOURCE_LOADER_CLASS
        return extension
    }

}
