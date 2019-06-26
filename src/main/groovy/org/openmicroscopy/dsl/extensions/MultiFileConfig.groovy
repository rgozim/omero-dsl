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
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.provider.Property
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory

@CompileStatic
class MultiFileConfig extends BaseFileConfig {

    /**
     * The output directory to write files to.
     * <p>
     * If outputDir is not absolute, it is relative to {@link DslExtension#outputDir}
     */
    final Property<File> outputDir


    private Transformer<? extends String, ? super SemanticType> formatOutput =
            new Transformer<String, SemanticType>() {
                @Override
                String transform(SemanticType semanticType) {
                    return semanticType.getShortname()
                }
            }

    MultiFileConfig(String name, Project project) {
        super(name, project)
        this.outputDir = project.objects.property(File)

        // Default output dir is the name of the configuration (e.g. java, combined)
        this.outputDir.convention(new File(name))
    }

    /**
     * see {@link #setOutputDir(java.io.File)}
     *
     * @param dir directory to set
     */
    void outputDir(File dir) {
        setOutputDir(dir)
    }

    /**
     * see {@link #setOutputDir(java.io.File)}
     *
     * @param dir directory to set
     */
    void outputDir(String dir) {
        setOutputDir(dir)
    }

    /**
     * see {@link #setOutputDir(java.io.File)}
     *
     * @param dir directory to set
     */
    void setOutputDir(String dir) {
        setOutputDir(new File(dir))
    }

    /**
     * Helper method to allow setting output dir using file type
     *
     * see {@link #outputDir}
     *
     * @param dir directory to set
     */
    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    /**
     * Allows the task of type {@link MultiFileGeneratorFactory} created
     * from this configuration to have control of the names of it's generated files
     *
     * <pre class='autoTested'>
     * formatOutput { SemanticType st -> {
     *        ${st.getPackage()}/${st.getShortname()}.java"
     * }
     * </pre>
     */
    void formatOutput(Transformer<? extends String, ? super SemanticType> transformer) {
        setFormatOutput transformer
    }

    /**
     * Allows the task of type {@link MultiFileGeneratorFactory} created
     * from this configuration to have control of the names of it's generated files
     *
     * <pre class='autoTested'>
     * formatOutput = { SemanticType st -> {
     *        ${st.getPackage()}/${st.getShortname()}.java"
     * }
     * </pre>
     */
    void setFormatOutput(Transformer<? extends String, ? super SemanticType> transformer) {
        formatOutput = transformer
    }

    Transformer<? extends String, ? super SemanticType> getFormatOutput() {
        return formatOutput
    }

}