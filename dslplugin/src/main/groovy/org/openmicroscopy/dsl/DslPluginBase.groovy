package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.ExtensionContainer
import org.openmicroscopy.dsl.extensions.CodeExtension
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.ResourceExtension
import org.openmicroscopy.dsl.extensions.VelocityExtension
import org.openmicroscopy.dsl.factories.CodeFactory
import org.openmicroscopy.dsl.factories.ResourceFactory
import org.openmicroscopy.dsl.tasks.DslMultiFileTask
import org.openmicroscopy.dsl.tasks.DslSingleFileTask

class DslPluginBase implements Plugin<Project> {

    static final String GROUP = "omero-dsl"
    static final String TASK_PREFIX = "generate"

    DslExtension dsl

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
        dsl = extensions.create('dsl', DslExtension, project)

        // Add NamedDomainObjectContainer for code and resource generators
        dsl.extensions.add("code", project.container(CodeExtension, new CodeFactory(project)))
        dsl.extensions.add("resource", project.container(ResourceExtension, new ResourceFactory(project)))

        // Create velocity inner extension for dsl
        velocityExt = dsl.extensions.create('velocity', VelocityExtension, project)
    }

    def configureCodeTasks(Project project) {
        dsl.code.all { CodeExtension op ->
            String taskName = TASK_PREFIX + op.name.capitalize()
            project.tasks.register(taskName, DslMultiFileTask) { t ->
                t.group = GROUP
                t.description = "parses ome.xml files and compiles velocity template"
                t.velocityProperties = velocityExt.data.get()
                t.formatOutput = op.formatOutput
                t.databaseType = dsl.databaseType
                t.databaseTypes = dsl.databaseTypes
                t.outputDir = handleFile(dsl.outputDir, op.outputDir)
                t.template = project.file(op.template) //getTemplate(dsl.templates, op.template)
                t.omeXmlFiles = getOmeXmlFiles(op.omeXmlFiles)
            }
        }
    }

    def configureResourceTasks(Project project) {
        dsl.resource.all { ResourceExtension op ->
            String taskName = TASK_PREFIX + op.name.capitalize()
            project.tasks.register(taskName, DslSingleFileTask) { t ->
                t.group = GROUP
                t.description = "parses ome.xml files and compiles velocity template"
                t.velocityProperties = velocityExt.data.get()
                t.databaseType = dsl.databaseType
                t.databaseTypes = dsl.databaseTypes
                t.outFile = handleFile(dsl.outputDir, op.outputFile)
                t.template = project.file(op.template) //getTemplate(dsl.templates, op.template)
                t.omeXmlFiles = getOmeXmlFiles(op.omeXmlFiles)
            }
        }
    }

    File handleFile(File dslFile, File singleFile) {
        if (!singleFile) {
            return dslFile
        }

        // If singleFile starts with the project root directory
        // then we know it is a full path to a file
        // singleFile.toPath().startsWith(project.rootDir.toPath())
        if (!dslFile || singleFile.isAbsolute()) {
            return singleFile
        }

        return new File(dslFile, "$singleFile")
    }

    File getTemplate(FileCollection collection, File file) {
        if (file.isAbsolute() && file.isFile()) {
            return file
        }
        return collection.getFiles().find { it.name == file.name }
    }

    FileCollection getOmeXmlFiles(FileCollection omeXmlFiles) {
        return dsl.omeXmlFiles + omeXmlFiles
    }

}
