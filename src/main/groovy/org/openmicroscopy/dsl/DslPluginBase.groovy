package org.openmicroscopy.dsl

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
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
import org.openmicroscopy.dsl.factories.DslFactory
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
        (omero as ExtensionAware).extensions.add(EXTENSION_DSL, createDslContainer(project))
    }

    void addGlobalConfigTasksMap(Project project) {
        // Add the map to extra properties
        // Access via project.fileGeneratorConfigMap
        project.extensions.extraProperties.set("fileGeneratorConfigMap", fileGeneratorConfigMap)
    }

    NamedDomainObjectContainer<DslExtension> createDslContainer(Project project, OmeroExtension omero) {
        def buildContainer =
                project.container(DslExtension, new DslFactory(project))

        buildContainer.configureEach { DslExtension dsl ->
            buildContainer.singleFile.configureEach { SingleFileConfig sfg ->
                addSingleFileGenTask(project, dsl, sfg)


            }

            buildContainer.multiFile.configureEach { MultiFileConfig mfg ->
                def task = addMultiFileGenTask(project, dsl, mfg, flavor)
                fileGeneratorConfigMap.put(task.name, mfg)
            }
        }

        return buildContainer
    }


    def addSingleFileGenTask(Project project, DslExtension dsl, SingleFileConfig sfc) {
        dsl.databases.get().each { String database ->
            String taskName = TASK_PREFIX_GENERATE + sfc.name.capitalize() + database
            project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
                @Override
                void execute(FileGeneratorTask t) {
                    t.with {
                        group = GROUP
                        outputFile.set(sfc.outputFile)
                        velocityConfig.set(dsl.velocity.data)
                        template.set(findTemplateProvider(dsl.templates, sfc.template))
                        databaseType.set(findDatabaseTypeProvider(dsl.databaseTypes, dsl.name))
                        mappingFiles.from(dsl.omeXmlFiles + sfc.omeXmlFiles)
                    }
                }
            })
            fileGeneratorConfigMap.put(taskName, sfc)
        }
    }

    def addMultiFileGenTask(Project project, DslExtension dsl, MultiFileConfig mfc) {
        dsl.databases.get().each { String database ->
            String taskName = TASK_PREFIX_GENERATE + mfc.name.capitalize() + database
            project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
                @Override
                void execute(FilesGeneratorTask t) {
                    t.with {
                        group = GROUP
                        formatOutput.set(mfc.formatOutput)
                        outputDir.set(mfc.outputDir)
                        velocityConfig.set(dsl.velocity.data)
                        template.set(findTemplateProvider(dsl.templates, mfc.template))
                        databaseType.set(findDatabaseTypeProvider(dsl.databaseTypes, dsl.name))
                        mappingFiles.from(dsl.omeXmlFiles + mfc.omeXmlFiles)
                    }
                }
            })
            fileGeneratorConfigMap.put(taskName, mfc)
        }
    }

    Provider<RegularFile> findDatabaseTypeProvider(FileCollection collection, String database) {
        providerFactory.provider(new Callable<RegularFile>() {
            @Override
            RegularFile call() throws Exception {
                RegularFileProperty result = objectFactory.fileProperty()
                result.set(findDatabaseType(collection, database))
                result.get()
            }
        })
    }

    Provider<RegularFile> findTemplateProvider(FileCollection collection, Property<File> file) {
        providerFactory.provider(new Callable<RegularFile>() {
            @Override
            RegularFile call() throws Exception {
                RegularFileProperty result = objectFactory.fileProperty()
                result.set(findTemplate(collection, file.get()))
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
