/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
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
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory
import org.openmicroscopy.dsl.factories.SingleFileGeneratorFactory
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
        def dsl = createDslExtension(project)

        // Add the map to extra properties
        // Access via project.fileGeneratorConfigMap
        project.extensions.extraProperties.set("fileGeneratorConfigMap", fileGeneratorConfigMap)

        dsl.multiFile.configureEach { MultiFileConfig mfg ->
            def task = addMultiFileGenTask(project, dsl, mfg)
            fileGeneratorConfigMap.put(task.name, mfg)
        }

        dsl.singleFile.configureEach { SingleFileConfig sfg ->
            def task = addSingleFileGenTask(project, dsl, sfg)
            fileGeneratorConfigMap.put(task.name, sfg)
        }
    }

    DslExtension createDslExtension(Project project) {
        def multiFileContainer =
                project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))

        project.extensions.create(EXTENSION_NAME_DSL, DslExtension, project,
                multiFileContainer, singleFileContainer)
    }

    TaskProvider<FilesGeneratorTask> addMultiFileGenTask(Project project, DslExtension dsl, MultiFileConfig ext) {
        String taskName = makeDslTaskName(ext.name, dsl.database.get())

        project.tasks.register(taskName, FilesGeneratorTask, new Action<FilesGeneratorTask>() {
            @Override
            void execute(FilesGeneratorTask t) {
                t.with {
                    group = GROUP
                    formatOutput.set(ext.formatOutput)
                    velocityConfig.set(dsl.velocity.data)
                    outputDir.set(getOutputDirProvider(dsl.outputDir, ext.outputDir))
                    template.set(findTemplateProvider(dsl.templates, ext.template))
                    databaseType.set(findDatabaseTypeProvider(dsl.databaseTypes, dsl.database))
                    mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
                }
            }
        })
    }

    TaskProvider<FileGeneratorTask> addSingleFileGenTask(Project project, DslExtension dsl, SingleFileConfig ext) {
        String taskName = makeDslTaskName(ext.name, dsl.database.get())

        project.tasks.register(taskName, FileGeneratorTask, new Action<FileGeneratorTask>() {
            @Override
            void execute(FileGeneratorTask t) {
                t.with {
                    group = GROUP
                    velocityConfig.set(dsl.velocity.data)
                    outputFile.set(getOutputFileProvider(dsl.outputDir, ext.outputFile))
                    template.set(findTemplateProvider(dsl.templates, ext.template))
                    databaseType.set(findDatabaseTypeProvider(dsl.databaseTypes, dsl.database))
                    mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
                }
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

    static String makeDslTaskName(String name, String databaseType) {
        TASK_PREFIX_GENERATE + name.capitalize() + databaseType.capitalize()
    }

}
