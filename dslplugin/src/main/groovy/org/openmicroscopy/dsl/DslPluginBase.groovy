package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
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

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
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
            String taskName = TASK_PREFIX_GENERATE + op.name.capitalize() + dsl.database.get().capitalize()
            project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
                @Override
                void execute(FilesGeneratorTask t) {
                    t.group = GROUP
                    t.velocityConfig = velocity.data
                    t.formatOutput = op.formatOutput
                    t.omeXmlFiles = dsl.omeXmlFiles + op.omeXmlFiles
                    t.template = findTemplateProvider(project, dsl.templates, op.template)
                    t.databaseType = findDatabaseTypeProvider(project, dsl.databaseTypes, dsl.database)
                    t.outputDir = getOutputDirProvider(dsl.outputDir, op.outputDir)
                }
            })
        }
    }

    static void configureResourceTasks(Project project, DslSpec dsl, VelocityExtension velocity) {
        dsl.singleFile.configureEach { SingleFileGeneratorExtension op ->
            String taskName = TASK_PREFIX_GENERATE + op.name.capitalize() + dsl.database.get().capitalize()
            project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
                @Override
                void execute(FileGeneratorTask t) {
                    t.group = GROUP
                    t.velocityConfig = velocity.data
                    t.omeXmlFiles = dsl.omeXmlFiles + op.omeXmlFiles
                    t.template = findTemplateProvider(project, dsl.templates, op.template)
                    t.databaseType = findDatabaseTypeProvider(project, dsl.databaseTypes, dsl.database)
                    t.outputFile = getOutputFileProvider(dsl.outputDir, op.outputFile)
                }
            })
        }
    }

    static Provider<Directory> getOutputDirProvider(DirectoryProperty baseDir, Property<File> childDir) {
        childDir.flatMap { File f -> baseDir.dir(f.toString()) }
    }

    static Provider<RegularFile> getOutputFileProvider(DirectoryProperty baseDir, Property<File> childFile) {
        childFile.flatMap { File f -> baseDir.file(f.toString()) }
    }

    static Provider<RegularFile> findDatabaseTypeProvider(Project project, ConfigurableFileCollection collection,
                                                          Property<String> type) {
        type.flatMap { String t ->
            RegularFileProperty result = project.objects.fileProperty()
            result.set(findDatabaseType(project, collection, t))
            result
        }
    }

    static Provider<RegularFile> findTemplateProvider(Project project, FileCollection collection,
                                                      Property<File> file) {
        file.flatMap { File f ->
            RegularFileProperty result = project.objects.fileProperty()
            result.set(findTemplate(project, collection, f))
            result
        }
    }

    static File findDatabaseType(Project project, FileCollection collection, String type) {
        if (!type) {
            throw new GradleException("Database type (psql, sql, oracle, etc) not specified")
        }

        File file = new File("$type-types.$FileTypes.EXTENSION_DB_TYPE")
        File databaseType = findInCollection(project, collection, file, FileTypes.PATTERN_DB_TYPE)
        if (!databaseType) {
            throw new GradleException("Can't find $file in collection of database types")
        }
        Log.info("Found database types file $databaseType")
        return databaseType
    }

    static File findTemplate(Project project, FileCollection collection, File file) {
        if (!file) {
            throw new GradleException("No template (.vm) specified")
        }

        if (file.isAbsolute() && file.isFile()) {
            return file
        }
        Log.info("Searching for template with file name: $file")
        File template = findInCollection(project, collection, file, FileTypes.PATTERN_TEMPLATE)
        if (!template) {
            throw new GradleException("Can't find $file in collection of templates")
        }
        Log.info("Found template file $template")
        return template
    }

    static File findInCollection(Project project, FileCollection collection, File file, String include) {
        Set<File> files = getFiles(project, collection, include)
        Log.info("Looking for file with name $file.name from the following")
        files.find { File f ->
            Log.info("$f")
            f.name == file.name
        }
    }

    static Set<File> getFiles(Project project, FileCollection collection, String include) {
        if (collection.isEmpty()) {
            throw new GradleException("Collection is empty")
        }
        FileTree src = project.files(collection).asFileTree
        src.matching(new PatternSet().include(include)).files
    }

}


//static File getOutputDir(File dslFile, File singleFile) {
//    if (!singleFile) {
//        return dslFile
//    }
//    // If singleFile starts with the project root directory
//    // then we know it is a full path to a file
//    if (!dslFile || singleFile.isAbsolute()) {
//        return singleFile
//    }
//    new File(dslFile, "$singleFile")
//}