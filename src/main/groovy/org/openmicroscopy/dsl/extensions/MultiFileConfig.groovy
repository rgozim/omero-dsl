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
import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory
import org.openmicroscopy.dsl.utils.SemanticTypeClosure
import org.openmicroscopy.dsl.utils.SemanticTypeTransformer

@CompileStatic
class MultiFileConfig extends BaseFileConfig {

    /**
     * The output directory to write files to.
     * <p>
     * If outputDir is not absolute, it is relative to {@link DslExtension#outputDir}
     */
    final Property<File> outputDir

    /**
     * Allows the task of type {@link MultiFileGeneratorFactory} created
     * from this configuration to have control of the names of it's generated files
     * <pre class='autoTested'>
     * formatOutput = { SemanticType st ->
     *    "${st.getPackage()}/${st.getShortname()}.java"
     * }
     * </pre>
     */
    final Property<MultiFileGenerator.FileNameFormatter> formatOutput

    MultiFileConfig(String name, Project project) {
        super(name, project)
        this.outputDir = project.objects.property(File)
        this.formatOutput = project.objects.property(MultiFileGenerator.FileNameFormatter)

        // Default output dir is the name of the configuration (e.g. java, combined)
        this.outputDir.convention(new File(name))
    }

    void outputDir(File dir) {
        setOutputDir(dir)
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        setOutputDir(new File(dir))
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void formatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        setFormatOutput(transformer)
    }

    void formatOutput(Closure closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        formatOutput.set(new SemanticTypeTransformer(transformer))
    }

    void setFormatOutput(Closure closure) {
        formatOutput.set(new SemanticTypeClosure(closure))
    }

}