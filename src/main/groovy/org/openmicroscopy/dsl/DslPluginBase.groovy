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
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.TaskProvider
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

    private final ObjectFactory objectFactory

    private final ProviderFactory providerFactory

    private DslExtension dsl

    private Project project

    @Inject
    DslPluginBase(ObjectFactory objectFactory, ProviderFactory providerFactory) {
        this.objectFactory = objectFactory
        this.providerFactory = providerFactory
    }

    @Override
    void apply(Project project) {
        this.project = project

        dsl = createDslExtension()
        dsl.multiFile.configureEach { addMultiFileGenTask(it) }
        dsl.singleFile.configureEach { addSingleFileGenTask(it) }
    }

    DslExtension createDslExtension() {
        def multiFileContainer =
                project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))

        project.extensions.create(EXTENSION_NAME_DSL, DslExtension, project,
                multiFileContainer, singleFileContainer)
    }

    TaskProvider<FilesGeneratorTask> addMultiFileGenTask(MultiFileConfig ext) {
        Provider<String> taskName = dsl.createTaskName(ext.name)

        project.tasks.register(taskName.get(), FilesGeneratorTask, new Action<FilesGeneratorTask>() {
            @Override
            void execute(FilesGeneratorTask task) {
                task.group = GROUP
                task.formatOutput.set(ext.formatOutput)
                task.velocityConfig.set(dsl.velocity.data)
                task.outputDir.set(getOutputDirProvider(dsl.outputDir, ext.outputDir))
                task.template.set(findTemplateProvider(dsl.templates, ext.template))
                task.databaseType.set(findDatabaseTypeProvider(dsl.databaseTypes, dsl.database))
                task.mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
            }
        })
    }

    TaskProvider<FileGeneratorTask> addSingleFileGenTask(SingleFileConfig ext) {
        Provider<String> taskName = dsl.createTaskName(ext.name)

        project.tasks.register(taskName.get(), FileGeneratorTask, new Action<FileGeneratorTask>() {
            @Override
            void execute(FileGeneratorTask task) {
                task.group = GROUP
                task.velocityConfig.set(dsl.velocity.data)
                task.outputFile.set(getOutputFileProvider(dsl.outputDir, ext.outputFile))
                task.template.set(findTemplateProvider(dsl.templates, ext.template))
                task.databaseType.set(findDatabaseTypeProvider(dsl.databaseTypes, dsl.database))
                task.mappingFiles.from(dsl.omeXmlFiles + ext.omeXmlFiles)
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
        type.map { String database ->
            File file = new File("$database-types.$FileTypes.EXTENSION_DB_TYPE")

            RegularFileProperty result = objectFactory.fileProperty()
            result.set(findInCollection(collection, file, FileTypes.PATTERN_DB_TYPE))
            result.get()
        }
    }

    Provider<RegularFile> findTemplateProvider(FileCollection collection, Property<File> fileProperty) {
        fileProperty.map { File file ->
            RegularFileProperty result = objectFactory.fileProperty()
            if (file.isAbsolute() && file.isFile()) {
                result.set(file)
            } else {
                result.set(findInCollection(collection, file, FileTypes.PATTERN_TEMPLATE))
            }
            result.get()
        }
    }

}
