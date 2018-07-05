import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class DslPlugin implements Plugin<Project> {

    Dsl dslExt
    VelocityExtension velocityExt

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
        dslExt = project.extensions.create('dsl', Dsl, project)

        // Create velocity inner extension for dsl
        velocityExt = dslExt.extensions.create('velocity', VelocityExtension, project)

        // Add NamedDomainObjectContainer for java configs
        dslExt.extensions.add("generate", project.container(DslOperation))
    }

    def configureVelocityExtension(Project project) {
        // Set some defaults for velocity
        velocityExt.loggerClassName = project.getLogger().getClass().getName()
    }

    def configureDslTasks(Project project) {
        dslExt.generate.all { DslOperation info ->
            DslTask task = createDslTask(project, info)

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
                task.group = "omero"
                task.description = "parses ome.xml files and compiles velocity template"
                task.profile = info.profile
                task.template = info.template
                task.omeXmlFiles = info.omeXmlFiles
                task.velocityProperties = project.dsl.velocity.data.get()

                // Add a clean task for cleanup
                addCleanTask(project, task)

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

    def addCleanTask(Project project, DslTask task) {
        if (task instanceof DslMultiFileTask) {
            def t = task as DslMultiFileTask
            project.clean {
                delete t.outputPath
            }
        } else if (task instanceof DslSingleFileTask) {
            def t = task as DslSingleFileTask
            project.clean {
                delete t.outFile
            }
        }
    }

    DslTask createDslTask(Project project, DslOperation dslOp) {
        def taskName = "dsl${dslOp.name.capitalize()}"

        if (dslOp.outputPath) {
            return project.tasks.create(taskName, DslMultiFileTask) {
                it.outputPath = dslOp.outputPath
                it.formatOutput = dslOp.formatOutput
            }
        } else if (dslOp.outputPath) {
            return project.tasks.create(taskName, DslSingleFileTask) {
                it.outFile = dslOp.outFile
            }
        } else {
            throw new GradleException("If this is a multi file " +
                    "generator, you need to set outputPath. " +
                    "Otherwise set outFile")
        }
    }
}
