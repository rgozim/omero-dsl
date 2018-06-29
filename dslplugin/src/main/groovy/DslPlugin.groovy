

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class DslPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        setupDsl(project)

        // Default configure dsl tasks
        configureVelocityExtension(project)
        configureDslTasks(project)
    }

    /**
     * Sets up the plugin language block
     * @param project
     */
    def setupDsl(Project project) {
        // Create the dsl extension
        Dsl dsl = project.extensions.create('dsl', Dsl, project)

        // Create velocity inner extension for dsl
        dsl.extensions.create('velocity', VelocityExtension, project)

        // Add NamedDomainObjectContainer for java configs
        dsl.extensions.add("generate", project.container(DslOperation))
    }

    def configureVelocityExtension(Project project) {
        VelocityExtension ve = project.dsl.velocity

        // Set some defaults for velocity
        ve.loggerClassName = project.getLogger().getClass().getName()
    }

    def configureDslTasks(Project project) {
        project.dsl.generate.all { DslOperation info ->
            String taskName = "dsl${info.name.capitalize()}"
            DslTask task = project.tasks.create(taskName, DslTask) {
                group = "omero"
                description = "parses ome.xml files and compiles velocity template"
            }

            project.afterEvaluate {
                // Combine template directory with template, if the
                // template is not a file (i.e a file name)
                if (project.dsl.templateDir) {
                    if (!info.template.isFile()) {
                        info.template = new File(project.dsl.templateDir.toString(),
                                info.template.toString())
                    }
                }

                if (project.dsl.mappingFiles) {
                    info.omeXmlFiles = project.dsl.mappingFiles
                }

                // Assign property values to task inputs
                task.outputPath = info.outputPath
                task.formatOutput = info.formatOutput
                task.outFile = info.outFile
                task.profile = info.profile
                task.template = info.template
                task.omeXmlFiles = info.omeXmlFiles
                task.velocityProperties = project.dsl.velocity.data.get()

                // Add results to clean tasks
                project.clean {
                    if (info.outputPath) {
                        delete info.outputPath
                    }

                    if (info.outFile) {
                        delete info.outFile
                    }
                }

                // Add dsl task to list of tasks
                if (project.plugins.hasPlugin(JavaPlugin)) {
                    // Ensure the DslTask runs before compileJava
                    project.tasks
                            .getByName("compileJava")
                            .dependsOn(task)
                }
            }
        }
    }
}
