package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

@CompileStatic
class DslMultiFileTask extends DslBaseTask {

    /**
     * Set this when you want to generate multiple files
     * Note: also requires setting {@link this.formatOutput}
     */
    @OutputDirectory
    File outputDir

    @Nested
    MultiFileGenerator.FileNameFormatter formatOutput

    void formatOutput(Closure closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(Closure closure) {
        formatOutput = new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return closure(t)
            }
        }
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        this.outputDir = project.file(dir)
    }

    void setOutputDir(File dir) {
        this.outputDir = dir
    }

    @Override
    protected Generator.Builder createGenerator() {
        return new MultiFileGenerator.Builder()
                .setOutputDir(outputDir)
                .setFileNameFormatter(formatOutput)
    }

}
