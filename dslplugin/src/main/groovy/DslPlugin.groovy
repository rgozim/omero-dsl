import extensions.CodeExtension
import extensions.DslExtension
import extensions.OperationExtension
import extensions.ResourceExtension
import extensions.VelocityExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import tasks.DslBaseTask
import tasks.DslMultiFileTask
import tasks.DslSingleFileTask

class DslPlugin implements Plugin<Project> {

    static final String GROUP = "omero-dsl"
    static final String TASK_PREFIX = "generate"

    DslExtension dslExt
    VelocityExtension velocityExt

    @Override
    void apply(Project project) {
        setupDsl(project)

        // Default configure dsl tasks
        configureVelocityExtension(project)
        configureCodeTasks(project)
        configureResourceTasks(project)
    }

    /**
     * Sets up the plugin language block
     * @param project
     */
    def setupDsl(Project project) {
        // Create the dsl extension
        dslExt = project.extensions.create('dsl', DslExtension, project)

        // Add NamedDomainObjectContainer for code and resource generators
        dslExt.extensions.add("code", project.container(CodeExtension))
        dslExt.extensions.add("resource", project.container(ResourceExtension))

        // Create velocity inner extension for dsl
        velocityExt = dslExt.extensions.create('velocity', VelocityExtension, project)
    }

    def configureVelocityExtension(Project project) {
        // Set some defaults for velocity
        velocityExt.loggerClassName = project.getLogger().getClass().getName()
    }

    def configureCodeTasks(Project project) {
        project.afterEvaluate {
            dslExt.code.all { CodeExtension op ->
                def taskName = TASK_PREFIX + op.name.capitalize()
                def task = project.tasks.create(taskName, DslMultiFileTask) {
                    group = GROUP
                    description = "parses ome.xml files and compiles velocity template"
                    formatOutput = op.formatOutput
                    outputPath = getOutput(op)
                    template = getTemplate(op)
                    omeXmlFiles = getOmeXmlFiles(op)
                    profile = op.profile
                    velocityProperties = velocityExt.data.get()
                }
                addCleanTask(project, task)
                addAfterCompileJava(project, task)
            }
        }
    }

    def configureResourceTasks(Project project) {
        project.afterEvaluate {
            dslExt.resource.all { ResourceExtension op ->
                def taskName = TASK_PREFIX + op.name.capitalize()
                def task = project.tasks.create(taskName, DslSingleFileTask) {
                    group = GROUP
                    description = "parses ome.xml files and compiles velocity template"
                    outFile = getOutput(op)
                    template = getTemplate(op)
                    omeXmlFiles = getOmeXmlFiles(op)
                    profile = op.profile
                    velocityProperties = velocityExt.data.get()
                }
                addCleanTask(project, task)
                addAfterCompileJava(project, task)
            }
        }
    }

    File getTemplate(OperationExtension dslOp) {
        if (!dslOp.template.isFile()) {
            return dslExt.templateFiles.find {
                it.name == dslOp.template.name
            }
        } else {
            return dslOp.template
        }
    }

    def getOutput(CodeExtension codeExt) {
        File file = codeExt.outputPath
        if (!file.isAbsolute()) {
            file = new File(dslExt.outputPath, file.path)
        }
        println "OuputPath for ${codeExt.name}: " + file.toString()
        return file
    }

    def getOutput(ResourceExtension resExt) {
        File file = resExt.outputFile
        if (!file.isAbsolute()) {
            file = new File(dslExt.outputPath, file.path)
        }
        println "OutputDir for ${resExt.name}: " + file.toString()
        return file
    }

    def getOmeXmlFiles(OperationExtension dsl) {
        if (dslExt.mappingFiles) {
            if (dsl.omeXmlFiles) {
                return dslExt.mappingFiles + dsl.omeXmlFiles
            } else {
                return dslExt.mappingFiles
            }
        }
        return dsl.omeXmlFiles
    }

    def addCleanTask(Project project, DslBaseTask task) {
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

    def addAfterCompileJava(Project project, DslBaseTask task) {
        // Add dsl task to list of tasks
        if (project.plugins.hasPlugin(JavaPlugin)) {
            // Ensure the tasks.DslBaseTask runs before compileJava
            project.tasks.getByName("compileJava")
                    .dependsOn(task)
        }
    }
}
