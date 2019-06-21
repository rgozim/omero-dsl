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
package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.Factory
import org.openmicroscopy.dsl.FileTypes

import javax.inject.Inject

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
abstract class GeneratorBaseTask extends DefaultTask {

    private final RegularFileProperty template = project.objects.fileProperty()

    private final RegularFileProperty databaseType = project.objects.fileProperty()

    private final Property<Properties> velocityConfig = project.objects.property(Properties)

    private final ConfigurableFileCollection mappingFiles = project.files()

    private final PatternFilterable omeXmlPatternSet

    GeneratorBaseTask() {
        omeXmlPatternSet = getPatternSetFactory().create()
                .include(FileTypes.PATTERN_OME_XML)
    }

    @Inject
    protected Factory<PatternSet> getPatternSetFactory() {
        throw new UnsupportedOperationException()
    }

    @TaskAction
    void apply() {
        // Create velocity engine with config
        VelocityEngine ve = new VelocityEngine()
        ve.init(velocityConfig.getOrElse(new Properties()))

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.template = template.get().asFile
        builder.omeXmlFiles = getOmeXmlFiles()
        builder.databaseTypes = getDatabaseTypes().get()
        builder.profile = getProfile().get()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    Set<File> getOmeXmlFiles() {
        FileTree src = this.mappingFiles.asFileTree
        return src.matching(omeXmlPatternSet).files
    }

    @Internal
    Provider<Properties> getDatabaseTypes() {
        databaseType.map { RegularFile file ->
            Properties databaseTypeProps = new Properties()
            file.asFile.withInputStream { databaseTypeProps.load(it) }
            databaseTypeProps
        }
    }

    @Internal
    Provider<String> getProfile() {
        // Determine database type
        databaseType.map { RegularFile file ->
            String fileName = file.asFile.name
            fileName.substring(0, fileName.lastIndexOf("-"))
        }
    }

    @InputFile
    RegularFileProperty getTemplate() {
        return template
    }

    @InputFile
    RegularFileProperty getDatabaseType() {
        return databaseType
    }

    @Input
    @Optional
    Property<Properties> getVelocityConfig() {
        return velocityConfig
    }

    @Internal
    ConfigurableFileCollection getMappingFiles() {
        return mappingFiles
    }

}
