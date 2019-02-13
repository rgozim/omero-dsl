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
import org.gradle.api.tasks.TaskProvider
import org.openmicroscopy.dsl.extensions.BaseFileConfig
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.MultiFileConfig
import org.openmicroscopy.dsl.extensions.SingleFileConfig
import org.openmicroscopy.dsl.factories.DslFactory
import org.openmicroscopy.dsl.tasks.FileGeneratorTask
import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

import javax.inject.Inject

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class DslPluginBase extends DslBase implements Plugin<Project> {

    public static final String GROUP = "omero-dsl"

    public static final String EXTENSION_NAME_DSL = "dsl"

    public static final String TASK_PREFIX_GENERATE = "generate"

    final Map<String, BaseFileConfig> fileGeneratorConfigMap = [:]

    private final ObjectFactory objectFactory

    private final ProviderFactory providerFactory

    private static final Logger Log = Logging.getLogger(DslPluginBase)

    @Inject
    DslPluginBase(ObjectFactory objectFactory, ProviderFactory providerFactory) {
        this.objectFactory = objectFactory
        this.providerFactory = providerFactory
    }

    @Override
    void apply(Project project) {
        // Add the map to extra properties
        // Access via project.fileGeneratorConfigMap
        project.extensions.extraProperties
                .set("fileGeneratorConfigMap", fileGeneratorConfigMap)

        def dslContainer = project.container(DslExtension, new DslFactory(project))

        dslContainer.whenObjectAdded { DslExtension dsl ->

            dsl.multiFile.whenObjectAdded { MultiFileConfig mfg ->
                def task = addMultiFileGenTask(project, dsl, mfg)
                fileGeneratorConfigMap.put(task.name, mfg)
            }

            dsl.singleFile.whenObjectAdded { SingleFileConfig sfg ->
                def task = addSingleFileGenTask(project, dsl, sfg)
                fileGeneratorConfigMap.put(task.name, sfg)
            }
        }
    }

    TaskProvider<FilesGeneratorTask> addMultiFileGenTask(Project project, DslExtension dsl, MultiFileConfig ext) {
        String taskName = TASK_PREFIX_GENERATE + ext.name.capitalize() + dsl.name.capitalize()

        project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
            @Override
            void execute(FilesGeneratorTask t) {
                t.with {
                    group = GROUP
                    formatOutput.set(ext.formatOutput)
                    velocityConfig.set(dsl.velocity.data)
                    outputDir.set(getOutputDirProvider(dsl.outputDir, ext.outputDir))
                    template.set(findTemplateProvider(dsl.templates, ext.template))
                    databaseType.set(findDatabaseType(dsl.databaseTypes, dsl.name))
                    mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
                }
            }
        })
    }

    TaskProvider<FileGeneratorTask> addSingleFileGenTask(Project project, DslExtension dsl, SingleFileConfig ext) {
        String taskName = TASK_PREFIX_GENERATE + ext.name.capitalize() + dsl.name.capitalize()

        project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
            @Override
            void execute(FileGeneratorTask t) {
                t.with {
                    group = GROUP
                    velocityConfig.set(dsl.velocity.data)
                    databaseType.set(findDatabaseType(dsl.databaseTypes, dsl.name))
                    template.set(findTemplateProvider(dsl.templates, ext.template))
                    outputFile.set(getOutputFileProvider(dsl.outputDir, ext.outputFile))


                    mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
                }
            }
        })
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
