package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.openmicroscopy.dsl.extensions.CodeExtension
import org.openmicroscopy.dsl.extensions.DslExtension
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
        dslExt.extensions.add("code", project.container(CodeExtension, { name ->
            new CodeExtension(name, project)
        }))
        dslExt.extensions.add("resource", project.container(ResourceExtension, {
            new ResourceExtension(it, project)
        }))

        // Create velocity inner extension for dsl
        velocityExt = dslExt.extensions.create('velocity', VelocityExtension, project)
    }

    def configureCodeTasks(Project project) {
        dslExt.code.all { CodeExtension op ->
            String taskName = TASK_PREFIX + op.name.capitalize()
            project.tasks.register(taskName, DslMultiFileTask) { t ->
                t.group = GROUP
                t.description = "parses ome.xml files and compiles velocity template"
                t.velocityProperties = velocityExt.data.get()
                t.profile = op.profile
                t.formatOutput = op.formatOutput
                t.outputDir = getOutput(op.outputDir)
                t.template = getTemplate(op.template)
                t.omeXmlFiles = getOmeXmlFiles(op.omeXmlFiles)
            }
        }
    }

    def configureResourceTasks(Project project) {
        dslExt.resource.all { ResourceExtension op ->
            String taskName = TASK_PREFIX + op.name.capitalize()
            project.tasks.register(taskName, DslSingleFileTask) { t ->
                t.group = GROUP
                t.description = "parses ome.xml files and compiles velocity template"
                t.velocityProperties = velocityExt.data.get()
                t.profile = op.profile
                t.outFile = getOutput(op.outputFile)
                t.template = getTemplate(op.template)
                t.omeXmlFiles = getOmeXmlFiles(op.omeXmlFiles)
            }
        }
    }

    File getTemplate(File template) {
        if (!template.isFile()) {
            return dslExt.templateFiles.files.find {
                it.name == template.name
            }
        } else {
            return template
        }
    }

    File getOutput(File output) {
        File file = output
        if (!file.isAbsolute()) {
            file = new File(dslExt.outputDir, file.path)
        }
        return file
    }

    FileCollection getOmeXmlFiles(FileCollection omeXmlFiles) {
        return dslExt.omeXmlFiles + omeXmlFiles
    }
}
