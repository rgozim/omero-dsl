package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.tasks.OutputFile

@CompileStatic
class FileGeneratorTask extends GeneratorBaseTask {

    /**
     * Set this when you only want to generate a single file
     */
    @OutputFile
    File outFile

    @Override
    protected Generator.Builder createGenerator() {
        return new SingleFileGenerator.Builder()
                .setOutFile(outFile)
    }

    void outFile(String outFile) {
        setOutFile(outFile)
    }

    void outFile(File outFile) {
        setOutFile(outFile)
    }

    void setOutFile(String outFile) {
        setOutFile(project.file(outFile))
    }

    void setOutFile(File outFile) {
        this.outFile = outFile
    }

}
