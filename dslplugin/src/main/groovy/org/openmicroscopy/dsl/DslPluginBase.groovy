package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.plugins.ExtensionAware
import org.openmicroscopy.dsl.extensions.CodeExtension
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.ResourceExtension
import org.openmicroscopy.dsl.extensions.VelocityExtension
import org.openmicroscopy.dsl.factories.CodeFactory
import org.openmicroscopy.dsl.factories.ResourceFactory
import org.openmicroscopy.dsl.tasks.DslMultiFileTask
import org.openmicroscopy.dsl.tasks.DslSingleFileTask

class DslPluginBase implements Plugin<Project> {

    public static final String GROUP = "omero-dsl"

    private static final String TASK_PREFIX = "generate"

    @Override
    void apply(Project project) {
        DslExtension dsl = createDslExtension(project)
        configure(project, dsl)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    DslExtension createDslExtension(Project project) {
        def code = project.container(CodeExtension, new CodeFactory(project))
        def resource = project.container(ResourceExtension, new ResourceFactory(project))

        // Create the dsl extension
        return project.extensions.create('dsl', DslExtension, project, code, resource)
    }

    static void configure(Project project, DslExtension dsl) {
        VelocityExtension velocity = createVelocityExtension(project, dsl)
        configureCodeTasks(project, dsl, velocity)
        configureResourceTasks(project, dsl, velocity)
    }

    static VelocityExtension createVelocityExtension(Project project, DslExtension dsl) {
        return ((ExtensionAware) dsl).extensions.create('velocity', VelocityExtension, project)
    }

    static void configureCodeTasks(Project project, DslExtension dsl, VelocityExtension velocity) {
        dsl.code.all { CodeExtension op ->
            String taskName = TASK_PREFIX + op.name.capitalize()
            project.tasks.register(taskName, DslMultiFileTask) { t ->
                t.group = GROUP
                t.description = "parses ome.xml files and compiles velocity template"
                t.velocityProperties = velocity.data.get()
                t.formatOutput = op.formatOutput
                t.databaseType = dsl.databaseType
                t.databaseTypes = dsl.databaseTypes
                t.outputDir = getOutputDir(dsl.outputDir, op.outputDir)
                t.template = getFileInCollection(dsl.templates, op.template)
                t.omeXmlFiles = dsl.omeXmlFiles + op.omeXmlFiles
            }
        }
    }

    static void configureResourceTasks(Project project, DslExtension dsl, VelocityExtension velocity) {
        dsl.resource.all { ResourceExtension op ->
            String taskName = TASK_PREFIX + op.name.capitalize()
            project.tasks.register(taskName, DslSingleFileTask) { t ->
                t.group = GROUP
                t.description = "parses ome.xml files and compiles velocity template"
                t.velocityProperties = velocity.data.get()
                t.databaseType = dsl.databaseType
                t.databaseTypes = dsl.databaseTypes
                t.outFile = getOutputDir(dsl.outputDir, op.outputFile)
                t.template = getFileInCollection(dsl.templates, op.template)
                t.omeXmlFiles = dsl.omeXmlFiles + op.omeXmlFiles
            }
        }
    }

    static File getOutputDir(File dslFile, File singleFile) {
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

    static File getFileInCollection(FileCollection collection, File file) {
        if (file.isAbsolute() && file.isFile()) {
            return file
        }
        return collection.getFiles().find { it.name == file.name }
    }

}
