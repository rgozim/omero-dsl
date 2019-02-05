package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile

@CompileStatic
class FilesGeneratorTask extends GeneratorBaseTask {

    /**
     * Set this when you want to generate multiple files
     * Note: also requires setting {@link this.formatOutput}
     */
    private final DirectoryProperty outputDir = objects.directoryProperty()

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

    @OutputDirectory
    DirectoryProperty getOutputDir() {
        return outputDir
    }

    void setOutputDir(String outFile) {
        setOutputDir(project.file(outFile))
    }

    void setOutputDir(Provider<File> outFile) {
        this.outputDir.set(project.layout.projectDirectory.dir(outFile))
    }

    void setOutputDir(Provider<? extends RegularFile> outFile) {
        this.outputFile.set(outFile)
    }

    void setOutputDir(RegularFile outFile) {
        this.outputFile.set(outFile)
    }

    void setOutputDir(File outFile) {
        this.outputFile.set(outFile)
    }

    @Override
    protected Generator.Builder createGenerator() {
        return new MultiFileGenerator.Builder()
                .setOutputDir(outputDir)
                .setFileNameFormatter(formatOutput)
    }

}
