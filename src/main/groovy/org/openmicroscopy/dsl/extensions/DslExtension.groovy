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

import static org.openmicroscopy.dsl.FileTypes.PATTERN_DB_TYPE
import static org.openmicroscopy.dsl.FileTypes.PATTERN_OME_XML
import static org.openmicroscopy.dsl.FileTypes.PATTERN_TEMPLATE

@CompileStatic
class DslExtension {

    private final Project project

    private final VelocityConfig velocity = new VelocityConfig()

    final NamedDomainObjectContainer<MultiFileConfig> multiFile

    final NamedDomainObjectContainer<SingleFileConfig> singleFile

    /**
     * Represents a collection of `.ome.xml` mapping files
     */
    final ConfigurableFileCollection omeXmlFiles

    /**
     * Represents a collection of `-types.properties` files
     */
    final ConfigurableFileCollection databaseTypes

    /**
     * Represents a collection of VelocityEngine `.vm` files
     */
    final ConfigurableFileCollection templates

    /**
     * The database type we want to build OMERO for (default is Postgres)
     */
    final Property<String> database

    /**
     * The output directory to write generated files to.
     * <p>
     * This can be overridden by using an absolute path in multiFile
     * or singleFile configs.
     */
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
        this.outputDir.convention(database.flatMap {
            project.layout.buildDirectory.dir("generated/sources/dsl/" + it)
        })
        this.omeXmlFiles.setFrom(project.fileTree(dir: "src/main/resources/mappings", include: PATTERN_OME_XML))
        this.databaseTypes.setFrom(project.fileTree(dir: "src/main/resources/properties", include: PATTERN_DB_TYPE))
        this.templates.setFrom(project.fileTree(dir: "src/main/resources/templates", include: PATTERN_TEMPLATE))
    }

    /**
     * Creates a name for task created from this extension.
     * <p>
     * It prefixes a string the beginning of {@param name} and appends {@code this.database}
     *
     * e.g. "combined" results in "generateCombinedPsql"
     *
     * @param name
     * @return the name used for creating tasks from this extension
     */
    String createTaskName(String name) {
        return DslPluginBase.TASK_PREFIX_GENERATE + name.capitalize() + database.get().capitalize()
    }

    /**
     * Configures the options of the VelocityEngine.
     *
     * See <a href="https://velocity.apache.org/engine/2.1/configuration.html">https://velocity.apache.org</a>
     *
     * <pre class='autoTested'>
     * dsl {
     *    velocityConfig {
     *        // Configure fields here
     *    }
     * }
     * </pre>
     *
     * @return
     */
    void velocityConfig(Action<? super VelocityConfig> action) {
        action.execute(this.velocity)
    }

    /**
     * Configures the container of possible source/code files to generate.
     *
     * <pre class='autoTested'>
     * apply plugin: 'org.openmicroscopy.dsl'
     *
     * dsl {
     *   multiFile {
     *     // Create a java source code generation using object.vm as template
     *     java {
     *          template "object.vm"
     *          outputDir "java"
     *          formatOutput = { st ->
     *             "${st.getPackage()}/${st.getShortname()}.java"
     *        }
     *     }
     * }
     * </pre>
     *
     * @param action
     */
    void multiFile(Action<? super NamedDomainObjectContainer<MultiFileConfig>> action) {
        action.execute(this.multiFile)
    }

    /**
     * Configures the container of possible resources to generate.
     *
     * <pre class='autoTested'>
     * apply plugin: 'org.openmicroscopy.dsl'
     *
     * dsl {
     *    singleFile {
     *     // Create a single resource file using cfg.vm as the velocity template
     *     hibernate {
     *         template "cfg.vm"
     *         outputFile "resources/hibernate.cfg.xml"
     *     }
     * }
     * </pre>
     *
     * @param action
     */
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

    VelocityConfig getVelocityConfig() {
        return velocity
    }
}
