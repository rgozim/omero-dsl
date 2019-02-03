package org.openmicroscopy.dsl


import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.tasks.util.PatternSet
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.MultiFileGeneratorExtension
import org.openmicroscopy.dsl.extensions.SingleFileGeneratorExtension
import org.openmicroscopy.dsl.extensions.VelocityExtension
import org.openmicroscopy.dsl.extensions.specs.DslSpec
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory
import org.openmicroscopy.dsl.factories.SingleFileGeneratorFactory
import org.openmicroscopy.dsl.tasks.FileGeneratorTask
import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

class DslPluginBase implements Plugin<Project> {

    static final String GROUP = "omero-dsl"
    static final String EXTENSION_NAME_DSL = "dsl"
    static final String EXTENSION_NAME_VELOCITY = "velocity"
    static final String TASK_PREFIX_GENERATE = "generate"

    private static final Logger Log = Logging.getLogger(DslPluginBase)

    @Override
    void apply(Project project) {
        DslExtension dsl = createDslExtension(project)
        VelocityExtension velocity = createVelocityExtension(project, dsl)
        configure(project, dsl, velocity)
    }

    @SuppressWarnings("GrMethodMayBeStatic")
    DslExtension createDslExtension(Project project) {
        def multiFileContainer =
                project.container(MultiFileGeneratorExtension, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileGeneratorExtension, new SingleFileGeneratorFactory(project))

        return project.extensions.create(EXTENSION_NAME_DSL, DslExtension, project,
                multiFileContainer, singleFileContainer)
    }

    static void configure(Project project, DslSpec dsl, VelocityExtension velocity) {
        configureCodeTasks(project, dsl, velocity)
        configureResourceTasks(project, dsl, velocity)
    }

    static VelocityExtension createVelocityExtension(Project project, DslSpec dsl) {
        return ((ExtensionAware) dsl).extensions.create(EXTENSION_NAME_VELOCITY, VelocityExtension, project)
    }

    static void configureCodeTasks(Project project, DslSpec dsl, VelocityExtension velocity) {
        dsl.multiFile.configureEach { MultiFileGeneratorExtension op ->
            String taskName = TASK_PREFIX_GENERATE + op.name.capitalize() + dsl.database.capitalize()
            project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
                @Override
                void execute(FilesGeneratorTask t) {
                    t.group = GROUP
                    t.velocityProperties = velocity.data.get()
                    t.formatOutput = op.formatOutput
                    t.outputDir = getOutputDir(dsl.outputDir, op.outputDir)
                    t.template = findFileInCollection(dsl.templates, op.template)
                    t.databaseType = findDatabaseType(dsl.databaseTypes, dsl.database)
                    t.omeXmlFiles = dsl.omeXmlFiles + op.omeXmlFiles
                }
            })
        }
    }

    static void configureResourceTasks(Project project, DslSpec dsl, VelocityExtension velocity) {
        dsl.singleFile.configureEach { SingleFileGeneratorExtension op ->
            String taskName = TASK_PREFIX_GENERATE + op.name.capitalize() + dsl.database.capitalize()
            project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
                @Override
                void execute(FileGeneratorTask t) {
                    t.group = GROUP
                    t.velocityProperties = velocity.data.get()
                    t.outFile = getOutputDir(dsl.outputDir, op.outputFile)
                    t.template = findFileInCollection(dsl.templates, op.template)
                    t.databaseType = findDatabaseType(dsl.databaseTypes, dsl.database)
                    t.omeXmlFiles = dsl.omeXmlFiles + op.omeXmlFiles
                }
            })
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

    static File findDatabaseType(FileCollection collection, String type) {
        if (collection.isEmpty()) {
            throw new GradleException("Database types not supplied to plugin")
        }

        if (!type) {
            throw new GradleException("Database type not specified")
        }

        final String filename = "$type-types.$FileTypes.EXTENSION_DB_TYPE"
        PatternSet pattern = new PatternSet().include(FileTypes.PATTERN_DB_TYPE)
        File found = collection.asFileTree.matching(pattern).files.find { it.name == filename }
        if (!found) {
            throw new GradleException("Can't find $filename in collection of database types")
        }
        return found
    }

    static File findFileInCollection(FileCollection collection, File file) {
        if (file.isAbsolute() && file.isFile()) {
            return file
        }
        return collection.files.find { it.name == file.name }
    }

}
