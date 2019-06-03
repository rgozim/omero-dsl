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
package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.openmicroscopy.dsl.DslPluginBase

import java.util.concurrent.Callable

import static org.openmicroscopy.dsl.FileTypes.PATTERN_DB_TYPE
import static org.openmicroscopy.dsl.FileTypes.PATTERN_OME_XML
import static org.openmicroscopy.dsl.FileTypes.PATTERN_TEMPLATE

@CompileStatic
class DslExtension {

    private final Project project

    final VelocityConfig velocity = new VelocityConfig()

    final NamedDomainObjectContainer<MultiFileConfig> multiFile

    final NamedDomainObjectContainer<SingleFileConfig> singleFile

    final ConfigurableFileCollection omeXmlFiles

    final ConfigurableFileCollection databaseTypes

    final ConfigurableFileCollection templates

    final Property<String> database

    final DirectoryProperty outputDir

    DslExtension(Project project,
                 NamedDomainObjectContainer<MultiFileConfig> multiFile,
                 NamedDomainObjectContainer<SingleFileConfig> singleFile) {
        this.project = project
        this.multiFile = multiFile
        this.singleFile = singleFile
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
        this.database = project.objects.property(String)
        this.outputDir = project.objects.directoryProperty()

        // Set some conventions
        this.database.convention("psql")
        this.outputDir.convention(project.layout.buildDirectory.dir("psql"))
        this.omeXmlFiles.setFrom(project.fileTree(dir: "src/main/resources/mappings", include: PATTERN_OME_XML))
        this.databaseTypes.setFrom(project.fileTree(dir: "src/main/resources/properties", include: PATTERN_DB_TYPE))
        this.templates.setFrom(project.fileTree(dir: "src/main/resources/templates", include: PATTERN_TEMPLATE))
    }

    /**
     * Creates a name for task created from this extension.
     * It prefixes a string the beginning of {@param name} and appends {@code this.database}
     *
     * e.g. "combined" results in "generateCombinedPsql"
     *
     * @param name
     * @return the name used for creating tasks from this extension
     */
    Provider<String> createTaskName(String name) {
        project.providers.provider(new Callable<String>() {
            @Override
            String call() throws Exception {
                return DslPluginBase.TASK_PREFIX_GENERATE + name.capitalize() + database.get().capitalize()
            }
        })
    }

    void multiFile(Action<? super NamedDomainObjectContainer<MultiFileConfig>> action) {
        action.execute(this.multiFile)
    }

    void singleFile(Action<? super NamedDomainObjectContainer<SingleFileConfig>> action) {
        action.execute(this.singleFile)
    }

    void omeXmlFiles(FileCollection files) {
        this.omeXmlFiles.from files
    }

    void setOmeXmlFiles(FileCollection files) {
        this.omeXmlFiles.setFrom(files)
    }

    void databaseTypes(FileCollection files) {
        this.databaseTypes.from files
    }

    void setDatabaseTypes(FileCollection files) {
        this.databaseTypes.setFrom files
    }

    void templates(FileCollection files) {
        this.templates.from files
    }

    void setTemplates(FileCollection files) {
        this.templates.setFrom files
    }

    void velocityConfig(Action<? super VelocityConfig> action) {
        setTemplates(action)
    }

    void setTemplates(Action<? super VelocityConfig> action) {
        action.execute(velocity)
    }

    void setOutputDir(Provider<? extends Directory> dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(Directory dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void database(String db) {
        this.database.set(db)
    }

}
