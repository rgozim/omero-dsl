package dslplugin

import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class DslPlugin implements Plugin<Project> {

    /**
     * Sets the group name for the DSLPlugin tasks to reside in.
     * i.e. In a terminal, call `./gradlew tasks` to list tasks in their groups in a terminal
     */
    final def GROUP = 'omero'

    Properties velocityProps

    @Override
    void apply(Project project) {
        setupDsl(project)
        configureVelocity(project)
        configJavaTasks(project)
    }

    void setupDsl(final Project project) {
        // Create the dsl extension
        project.extensions.create('dsl', Dsl)

        // Create velocity inner extension for dsl
        project.dsl.extensions.create('velocity', VelocityExtension)

        // Add NamedDomainObjectContainer for java configs
        project.dsl.extensions.add("generate", project.container(DslOperation))
    }

    void configureVelocity(final Project project) {
        project.afterEvaluate {
            VelocityExtension ve = project.dsl.velocity
            if (!ve.logger_class_name) {
                ve.logger_class_name = project.getLogger().getClass().getName()
            }
            velocityProps = createVelocityProperties(ve)
        }
    }

    void configJavaTasks(final Project project) {
        project.dsl.generate.all { DslOperation info ->
            def taskName = "process${info.name.capitalize()}"

            // Create task and assign group name
            def task = project.task(taskName, type: DslTask) {
                group = GROUP
                description = 'parses ome.xml files and compiles velocity template'
            }

            // Assign property values to task inputs
            project.afterEvaluate {
                task.velocityProps = velocityProps
                task.template = new File(info.template)
                task.omeXmlFiles = info.omeXmlFiles
                task.outputPath = info.outputPath
                task.outFile = info.outFile
                task.formatOutput = info.formatOutput
            }

            if (project.plugins.hasPlugin(JavaPlugin)) {
                // Ensure the dsltask runs before compileJava
                project.tasks.getByName("compileJava").dependsOn(taskName)
            }
        }
    }

    /**
     * If the velocity properties include a path to a folder containing .vm files
     * this method will combine the path of that folder with the name of the template
     * file we're interested in. This is a workaround so that Gradle can monitor
     * the .vm file for any changes and thus trigger an incremental build.
     * @param props velocity properties
     * @param templateName name of template file
     * @return
     */
    static File determineTemplateFileLocation(Properties props, String templateName) {
        String templateDir = props.getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH)
        if (templateDir == null) {
            return new File(templateName)
        }
        return new File(templateDir, templateName)
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

