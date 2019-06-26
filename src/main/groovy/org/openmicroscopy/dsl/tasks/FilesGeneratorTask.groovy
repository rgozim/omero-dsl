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
import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class FilesGeneratorTask extends GeneratorBaseTask {

    /**
     * Set this when you want to generate multiple files
     * Note: also requires setting {@link this.formatOutput}
     */
    private final DirectoryProperty outputDir = project.objects.directoryProperty()

    /**
     * Default callback returns SemanticType.shortName
     */
    private Property<MultiFileGenerator.FileNameFormatter> formatOutput =
            project.objects.property(MultiFileGenerator.FileNameFormatter)

    @Override
    protected Generator.Builder createGenerator() {
        return new MultiFileGenerator.Builder()
                .setOutputDir(outputDir.get().asFile)
                .setFileNameFormatter(formatOutput.get())
    }

    @OutputDirectory
    DirectoryProperty getOutputDir() {
        return this.outputDir
    }

    @Nested
    Provider<MultiFileGenerator.FileNameFormatter> getFormatOutput() {
        return formatOutput
    }

    void formatOutput(Transformer<? extends String, ? super SemanticType> transformer) {
        setFormatOutput(transformer)
    }

    void setFormatOutput(Transformer<? extends String, ? super SemanticType> transformer) {
        this.formatOutput.set(new FileNameFormatter(transformer))
    }

    private static class FileNameFormatter implements MultiFileGenerator.FileNameFormatter {
        Transformer<? extends String, ? super SemanticType> transformer

        FileNameFormatter(Transformer<? extends String, ? super SemanticType> transformer) {
            this.transformer = transformer
        }

        @Override
        String format(SemanticType semanticType) {
            return transformer.transform(semanticType)
        }
    }

}
