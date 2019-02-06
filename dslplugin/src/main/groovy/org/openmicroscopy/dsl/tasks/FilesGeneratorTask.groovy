package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
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

    @Nested
    MultiFileGenerator.FileNameFormatter formatOutput

    @Override
    protected Generator.Builder createGenerator() {
        return new MultiFileGenerator.Builder()
                .setOutputDir(outputDir.get().asFile)
                .setFileNameFormatter(formatOutput)
    }

    @OutputDirectory
    DirectoryProperty getOutputDir() {
        return outputDir
    }

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

    void setOutputDir(String outFile) {
        setOutputDir(project.file(outFile))
    }

    void setOutputDir(Provider<? extends Directory> outFile) {
        this.outputDir.set(outFile)
    }

    void setOutputDir(Directory outFile) {
        this.outputDir.set(outFile)
    }

    void setOutputDir(File outFile) {
        this.outputDir.set(outFile)
    }



}
