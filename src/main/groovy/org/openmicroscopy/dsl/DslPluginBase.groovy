package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
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
import org.gradle.api.tasks.TaskProvider
import org.openmicroscopy.dsl.extensions.BaseFileConfig
import org.openmicroscopy.dsl.extensions.MultiFileConfig
import org.openmicroscopy.dsl.extensions.OmeroExtension
import org.openmicroscopy.dsl.extensions.SingleFileConfig
import org.openmicroscopy.dsl.extensions.VariantExtension
import org.openmicroscopy.dsl.factories.DslFactory
import org.openmicroscopy.dsl.tasks.FileGeneratorTask
import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

import javax.inject.Inject
import java.util.concurrent.Callable

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class DslPluginBase extends DslBase implements Plugin<Project> {

    public static final String GROUP = "omero-build"

    public static final String EXTENSION_BUILD = "build"

    public static final String TASK_PREFIX_GENERATE = "generate"

    private final Map<String, BaseFileConfig> fileGeneratorConfigMap = [:]

    private final ObjectFactory objectFactory

    private final ProviderFactory providerFactory

    private NamedDomainObjectContainer<VariantExtension> build

    private static final Logger Log = Logging.getLogger(DslPluginBase)

    @Inject
    DslPluginBase(ObjectFactory objectFactory, ProviderFactory providerFactory) {
        this.objectFactory = objectFactory
        this.providerFactory = providerFactory
    }

    @Override
    void apply(Project project) {
        // Apply omero plugin
        // project.plugins.apply(OmeroPlugin)

        project.extensions.create("omero", OmeroExtension)

        addGlobalConfigTasksMap(project)

        OmeroExtension omero = project.extensions.getByType(OmeroExtension)
        build = createDslExtensions(project, omero)
        build.all { VariantExtension variant ->
            variant.multiFile.configureEach { MultiFileConfig mfg ->
                def task = addMultiFileGenTask(project, variant, mfg)
                fileGeneratorConfigMap.put(task.name, mfg)
            }

            variant.singleFile.configureEach { SingleFileConfig sfg ->
                def task = addSingleFileGenTask(project, variant, sfg)
                fileGeneratorConfigMap.put(task.name, sfg)
            }
        }
    }

    NamedDomainObjectContainer<VariantExtension> getBuild() {
        return build
    }

    void addGlobalConfigTasksMap(Project project) {
        // Add the map to extra properties
        // Access via project.fileGeneratorConfigMap
        project.extensions.extraProperties.set("fileGeneratorConfigMap", fileGeneratorConfigMap)
    }

    NamedDomainObjectContainer<VariantExtension> createDslExtensions(Project project, OmeroExtension omero) {
        def buildContainer = project.container(VariantExtension, new DslFactory(project))
        (omero as ExtensionAware).extensions.add(EXTENSION_BUILD, buildContainer)
        return buildContainer
    }

    TaskProvider<FilesGeneratorTask> addMultiFileGenTask(Project project, VariantExtension variant, MultiFileConfig ext) {
        String taskName = TASK_PREFIX_GENERATE + ext.name.capitalize() + variant.name.capitalize()

        project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
            @Override
            void execute(FilesGeneratorTask t) {
                t.with {
                    group = GROUP
                    formatOutput.set(ext.formatOutput)
                    outputDir.set(ext.outputDir)
                    velocityConfig.set(variant.velocity.data)
                    template.set(findTemplateProvider(variant.templates, ext.template))
                    databaseType.set(findDatabaseTypeProvider(variant.databaseTypes, variant.name))
                    mappingFiles.from(variant.omeXmlFiles + ext.omeXmlFiles)
                }
            }
        })
    }

    TaskProvider<FileGeneratorTask> addSingleFileGenTask(Project project, VariantExtension variant, SingleFileConfig ext) {
        String taskName = TASK_PREFIX_GENERATE + ext.name.capitalize() + variant.name.capitalize()

        project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
            @Override
            void execute(FileGeneratorTask t) {
                t.with {
                    group = GROUP
                    outputFile.set(ext.outputFile)
                    velocityConfig.set(variant.velocity.data)
                    template.set(findTemplateProvider(variant.templates, ext.template))
                    databaseType.set(findDatabaseTypeProvider(variant.databaseTypes, variant.name))
                    mappingFiles.from(variant.omeXmlFiles + ext.omeXmlFiles)
                }
            }
        })
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
        file.map { File f ->
            RegularFileProperty result = objectFactory.fileProperty()
            result.set(findTemplate(collection, f))
            result.get()
        }
    }

}
