package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.MultiFileGeneratorExtension
import org.openmicroscopy.dsl.extensions.SingleFileGeneratorExtension
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory
import org.openmicroscopy.dsl.factories.SingleFileGeneratorFactory
import org.openmicroscopy.dsl.tasks.FileGeneratorTask
import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

import javax.inject.Inject

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class DslPluginBase extends DslBase implements Plugin<Project> {

    static final String GROUP = "omero-dsl"
    static final String EXTENSION_NAME_DSL = "dsl"
    static final String TASK_PREFIX_GENERATE = "generate"

    private static final Logger Log = Logging.getLogger(DslPluginBase)

    private final ObjectFactory objectFactory

    private final ProviderFactory providerFactory

    @Inject
    DslPluginBase(ObjectFactory objectFactory, ProviderFactory providerFactory) {
        this.objectFactory = objectFactory
        this.providerFactory = providerFactory
    }

    @Override
    void apply(Project project) {
        DslExtension dsl = project.extensions.findByType(DslExtension)
        if (!dsl) {
            // Only create this extension if a higher up extension hasn't already
            // created one (i.e. blitz plugin)
            dsl = createDslExtension(project)
        }

        configure(project, dsl)
    }

    DslExtension createDslExtension(Project project) {
        def multiFileContainer =
                project.container(MultiFileGeneratorExtension, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileGeneratorExtension, new SingleFileGeneratorFactory(project))

        project.extensions.create(EXTENSION_NAME_DSL, DslExtension, project,
                multiFileContainer, singleFileContainer)
    }

    void configure(Project project, DslExtension dsl) {
        configureCodeTasks(project, dsl)
        configureResourceTasks(project, dsl)
    }

    void configureCodeTasks(Project project, DslExtension dsl) {
        dsl.multiFile.configureEach { MultiFileGeneratorExtension mfg ->
            addMultiFileGenTask(project, dsl, mfg)
        }
    }

    void configureResourceTasks(Project project, DslExtension dsl) {
        dsl.singleFile.configureEach { SingleFileGeneratorExtension sfg ->
            addSingleFileGenTask(project, dsl, sfg)
        }
    }

    void addMultiFileGenTask(Project project, DslExtension dsl, MultiFileGeneratorExtension ext) {
        String taskName = TASK_PREFIX_GENERATE + ext.name.capitalize() + dsl.database.get().capitalize()
        project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
            @Override
            void execute(FilesGeneratorTask t) {
                t.group = GROUP
                t.formatOutput = ext.formatOutput
                t.velocityConfig = dsl.velocity
                t.omeXmlFiles = dsl.omeXmlFiles + ext.omeXmlFiles
                t.outputDir = getOutputDirProvider(dsl.outputDir, ext.outputDir)
                t.template = findTemplateProvider(dsl.templates, ext.template)
                t.databaseType = findDatabaseTypeProvider(dsl.databaseTypes, dsl.database)
            }
        })
    }

    void addSingleFileGenTask(Project project, DslExtension dsl, SingleFileGeneratorExtension ext) {
        String taskName = TASK_PREFIX_GENERATE + ext.name.capitalize() + dsl.database.get().capitalize()
        project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
            @Override
            void execute(FileGeneratorTask t) {
                t.group = GROUP
                t.velocityConfig = dsl.velocity
                t.omeXmlFiles = dsl.omeXmlFiles + ext.omeXmlFiles
                t.outputFile = getOutputFileProvider(dsl.outputDir, ext.outputFile)
                t.template = findTemplateProvider(dsl.templates, ext.template)
                t.databaseType = findDatabaseTypeProvider(dsl.databaseTypes, dsl.database)
            }
        })
    }

    Provider<Directory> getOutputDirProvider(DirectoryProperty baseDir, Property<File> childDir) {
        childDir.flatMap { File f -> baseDir.dir(f.toString()) }
    }

    Provider<RegularFile> getOutputFileProvider(DirectoryProperty baseDir, Property<File> childFile) {
        childFile.flatMap { File f -> baseDir.file(f.toString()) }
    }

    Provider<RegularFile> findDatabaseTypeProvider(FileCollection collection, Property<String> type) {
        type.map { String t ->
            RegularFileProperty result = objectFactory.fileProperty()
            result.set(findDatabaseType(collection, type.get()))
            result.get()
        }
    }

    Provider<RegularFile> findTemplateProvider(FileCollection collection, Property<File> file) {
        file.map { File f ->
            RegularFileProperty result = objectFactory.fileProperty()
            result.set(findTemplate(collection, f))
            result.get()
        }
    }

}
