package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.gradle.api.tasks.Delete
import org.openmicroscopy.dsl.extensions.CodeExtension
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.OperationExtension
import org.openmicroscopy.dsl.extensions.ResourceExtension
import org.openmicroscopy.dsl.extensions.VelocityExtension
import org.openmicroscopy.dsl.tasks.DslMultiFileTask
import org.openmicroscopy.dsl.tasks.DslSingleFileTask

class DslPluginBase implements Plugin<Project> {

    static final String GROUP = "omero-dsl"
    static final String TASK_PREFIX = "generate"

    DslExtension dslExt
    VelocityExtension velocityExt

    @Override
    void apply(Project project) {
        init(project)
    }

    void init(Project project) {
        this.init(project, project.extensions)
    }

    void init(Project project, ExtensionContainer baseExt) {
        setupDsl(project, baseExt)

        // Default configure dsl org.openmicroscopy.dsl.tasks
        configureVelocityExtension(project)
        configureCodeTasks(project)
        configureResourceTasks(project)
    }

    /**
     * Sets up the plugin language block
     * @param project
     */
    def setupDsl(Project project, ExtensionContainer extensions) {
        // Create the dsl extension
        dslExt = extensions.create('dsl', DslExtension, project)

        // Add NamedDomainObjectContainer for code and resource generators
        dslExt.extensions.add("code", project.container(CodeExtension, {
            new CodeExtension(it, project)
        }))
        dslExt.extensions.add("resource", project.container(ResourceExtension, {
            new ResourceExtension(it, project)
        }))

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
                String taskName = TASK_PREFIX + op.name.capitalize()
                DslMultiFileTask task = project.tasks.create(taskName, DslMultiFileTask) {
                    group = GROUP
                    description = "parses ome.xml files and compiles velocity template"
                    velocityProperties = velocityExt.data.get()
                    profile = op.profile
                    formatOutput = op.formatOutput
                    outputPath = getOutput(op)
                    template = getTemplate(op)
                    omeXmlFiles = getOmeXmlFiles(op)
                }
                addCleanTask(project, taskName, task.outputPath)
            }
        }
    }

    def configureResourceTasks(Project project) {
        project.afterEvaluate {
            dslExt.resource.all { ResourceExtension op ->
                String taskName = TASK_PREFIX + op.name.capitalize()
                DslSingleFileTask task = project.tasks.create(taskName, DslSingleFileTask) {
                    group = GROUP
                    description = "parses ome.xml files and compiles velocity template"
                    profile = op.profile
                    velocityProperties = velocityExt.data.get()
                    outFile = getOutput(op)
                    template = getTemplate(op)
                    omeXmlFiles = getOmeXmlFiles(op)
                }
                addCleanTask(project, taskName, task.outFile)
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

    File getOutput(CodeExtension codeExt) {
        File file = codeExt.outputPath
        if (!file.isAbsolute()) {
            file = new File(dslExt.outputPath, file.path)
        }
        println "OuputPath for ${codeExt.name}: " + file.toString()
        return file
    }

    File getOutput(ResourceExtension resExt) {
        File file = resExt.outputFile
        if (!file.isAbsolute()) {
            file = new File(dslExt.outputPath, file.path)
        }
        println "OutputDir for ${resExt.name}: " + file.toString()
        return file
    }

    FileCollection getOmeXmlFiles(OperationExtension dsl) {
        if (dsl.omeXmlFiles) {
            return dsl.omeXmlFiles
        } else {
            return dslExt.omeXmlFiles
        }
    }

    def addCleanTask(Project project, String taskName, File toDelete) {
        String cleanTaskName = "clean${taskName.capitalize()}"
        project.tasks.create(cleanTaskName, Delete) {
            group GROUP
            delete toDelete
            shouldRunAfter project.tasks.getByName('clean')
        }
    }
}
