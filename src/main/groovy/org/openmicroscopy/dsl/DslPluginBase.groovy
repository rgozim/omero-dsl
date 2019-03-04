package org.openmicroscopy.dsl

import org.gradle.api.Action
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.openmicroscopy.dsl.extensions.BaseFileConfig
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.MultiFileConfig
import org.openmicroscopy.dsl.extensions.OmeroExtension
import org.openmicroscopy.dsl.extensions.SingleFileConfig
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory
import org.openmicroscopy.dsl.factories.SingleFileGeneratorFactory
import org.openmicroscopy.dsl.tasks.FileGeneratorTask
import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

import javax.inject.Inject
import java.util.concurrent.Callable

@SuppressWarnings("UnstableApiUsage")
class DslPluginBase extends DslBase implements Plugin<Project> {

    public static final String GROUP = "omero-build"

    public static final String EXTENSION_DSL = "dsl"

    public static final String TASK_PREFIX_GENERATE = "generate"

    private final Map<String, BaseFileConfig> fileGeneratorConfigMap = [:]

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
        project.plugins.apply(OmeroPlugin)

        addGlobalConfigTasksMap(project)

        OmeroExtension omero = project.extensions.getByType(OmeroExtension)

        DslExtension dsl = createDslExtension(project, omero)

        dsl.singleFile.configureEach { SingleFileConfig config ->
            addSingleFileGenTask(project, dsl, config)
        }

        dsl.multiFile.configureEach { MultiFileConfig config ->
            addMultiFileGenTask(project, dsl, config)
        }
    }

    void addGlobalConfigTasksMap(Project project) {
        // Add the map to extra properties
        // Access via project.fileGeneratorConfigMap
        project.extensions.extraProperties.set("fileGeneratorConfigMap", fileGeneratorConfigMap)
    }

    DslExtension createDslExtension(Project project, OmeroExtension omero) {
        def singleFileContainer =
                project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))

        def multiFileContainer =
                project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))

        (omero as ExtensionAware).extensions.create(EXTENSION_DSL, DslExtension, project,
                singleFileContainer, multiFileContainer)
    }


    void addMultiFileGenTask(Project project, DslExtension dsl, MultiFileConfig ext) {
        dsl.databases.get().each { String database ->
            String taskName =
                    TASK_PREFIX_GENERATE + ext.name.capitalize() + database.capitalize()

            Provider<Directory> baseOutputDir = dsl.outputDir.dir(database)

            project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
                @Override
                void execute(FilesGeneratorTask t) {
                    t.with {
                        group = GROUP
                        formatOutput.set(ext.formatOutput)
                        velocityConfig.set(dsl.velocity.data)
                        outputDir.set(outputDirProvider(baseOutputDir, ext.outputDir))
                        template.set(templateProvider(dsl.templates, ext.template))
                        databaseType.set(databaseTypeProvider(dsl.databaseTypes, database))
                        mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
                    }
                }
            })
        }
    }

    void addSingleFileGenTask(Project project, DslExtension dsl, SingleFileConfig ext) {
        dsl.databases.get().each { String database ->
            String taskName =
                    TASK_PREFIX_GENERATE + ext.name.capitalize() + database.capitalize()

            Provider<Directory> baseOutputDir = dsl.outputDir.dir(database)

            project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
                @Override
                void execute(FileGeneratorTask t) {
                    t.with {
                        group = GROUP
                        velocityConfig.set(dsl.velocity.data)
                        outputFile.set(outputFileProvider(baseOutputDir, ext.outputFile))
                        template.set(templateProvider(dsl.templates, ext.template))
                        databaseType.set(databaseTypeProvider(dsl.databaseTypes, database))
                        mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
                    }
                }
            })
        }
    }

    Provider<Directory> outputDirProvider(Provider<Directory> baseDir, Property<File> childDir) {
        childDir.map { File f -> baseDir.get().dir(f.toString()) }
    }

    Provider<RegularFile> outputFileProvider(Provider<Directory> baseDir, Property<File> childFile) {
        childFile.map { File f -> baseDir.get().file(f.toString()) }
    }

    Provider<RegularFile> templateProvider(FileCollection collection, Property<File> file) {
        file.map { File f ->
            RegularFileProperty result = objectFactory.fileProperty()
            result.set(findTemplate(collection, f))
            result.get()
        }
    }

    Provider<RegularFile> databaseTypeProvider(FileCollection collection, String type) {
        providerFactory.provider(new Callable<RegularFile>() {
            @Override
            RegularFile call() throws Exception {
                RegularFileProperty result = objectFactory.fileProperty()
                result.set(findDatabaseType(collection, type))
                result.get()
            }
        })
    }

}


//NamedDomainObjectContainer<DslExtension> createBuildContainer(Project project) {
//    def buildContainer =
//            project.container(DslExtension, new DslFactory(project))
//
//    def singleFileContainer =
//            project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))
//
//    def multiFileContainer =
//            project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))
//
//    buildContainer.configureEach { DslExtension variant ->
//        singleFileContainer.configureEach { SingleFileConfig sfg ->
//            def task = addSingleFileGenTask(project, variant, sfg)
//            fileGeneratorConfigMap.put(task.name, sfg)
//        }
//
//        multiFileContainer.configureEach { MultiFileConfig mfg ->
//            def task = addMultiFileGenTask(project, variant, mfg)
//            fileGeneratorConfigMap.put(task.name, mfg)
//        }
//    }
//
//    buildContainer.extensions.add("singleFile", singleFileContainer)
//
//    return buildContainer
//}
