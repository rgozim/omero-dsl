package org.openmicroscopy.dsl.tasks

import ome.dsl.velocity.Generator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.tasks.OutputFile

class DslSingleFileTask extends DslBaseTask {

    /**
     * Set this when you only want to generate a single file
     */
    @OutputFile
    File outFile

    void setOutFile(File outFile) {
        this.outFile = setAbsPath(outFile)
    }

    void outFile(File outFile) {
        setOutFile(outFile)
    }

    @Override
    protected Generator.Builder createGenerator() {
        return new SingleFileGenerator.Builder()
                .setOutFile(outFile)
    }
}
