package dslplugin

import org.apache.velocity.runtime.RuntimeConstants
import org.apache.velocity.runtime.resource.loader.FileResourceLoader
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class DslPluginBase implements Plugin<Project> {

    @Override
    void apply(Project project) {
        setupDsl(project)
        configureVelocity(project)
    }

    /**
     * Sets up the plugin language block
     * @param project
     */
    void setupDsl(final Project project) {
        // Create the dsl extension
        project.extensions.create('dsl', Dsl)

        // Create velocity inner extension for dsl
        project.dsl.extensions.create('velocity', VelocityExtension)

        // Add NamedDomainObjectContainer for java configs
        project.dsl.extensions.add("generate", project.container(DslOperation))
    }

    /**
     * Sets up default values for Velocity configuration
     * @param project
     */
    void configureVelocity(final Project project) {
        // Set some defaults
        VelocityExtension ve = project.dsl.velocity
        ve.resource_loader = 'file'
        ve.resource_loader_class = ['file.resource.loader.class': FileResourceLoader.class.getName()]
        ve.logger_class_name = project.getLogger().getClass().getName()
        if (project.plugins.hasPlugin(JavaPlugin)) {
            ve.file_resource_loader_path = "${project.sourceSets.main.output.resourcesDir}"
        } else {
            ve.file_resource_loader_path = "${project.projectDir}"
        }
    }

    static Properties createVelocityProperties(VelocityExtension extension) {
        final def props = new Properties()

        if (extension.logger_class_name) {
            props.setProperty(RuntimeConstants.RUNTIME_LOG_NAME,
                    extension.logger_class_name)
        }

        if (extension.resource_loader) {
            props.setProperty(RuntimeConstants.RESOURCE_LOADER,
                    extension.resource_loader)
        }

        if (extension.file_resource_loader_path) {
            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH,
                    extension.file_resource_loader_path)
        }

        if (extension.file_resource_loader_cache) {
            props.setProperty(RuntimeConstants.FILE_RESOURCE_LOADER_CACHE,
                    extension.file_resource_loader_cache as String)
        }

        if (extension.resource_loader_class) {
            extension.resource_loader_class.each { String k, String v ->
                props.setProperty(k, v)
            }
        }

        return props
    }
}
