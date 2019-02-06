package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputFile

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class FileGeneratorTask extends GeneratorBaseTask {

    /**
     * Set this when you only want to generate a single file
     */
    private final RegularFileProperty outputFile = project.objects.fileProperty()

    @Override
    protected Generator.Builder createGenerator() {
        return new SingleFileGenerator.Builder()
                .setOutFile(outputFile.get().asFile)
    }

    @OutputFile
    RegularFileProperty getOutputFile() {
        return outputFile
    }

    void setOutputFile(String outFile) {
        setOutputFile(project.file(outFile))
    }

    void setOutputFile(Provider<? extends RegularFile> outFile) {
        this.outputFile.set(outFile)
    }

    void setOutputFile(RegularFile outFile) {
        this.outputFile.set(outFile)
    }

    void setOutputFile(File outFile) {
        this.outputFile.set(outFile)
    }

}
