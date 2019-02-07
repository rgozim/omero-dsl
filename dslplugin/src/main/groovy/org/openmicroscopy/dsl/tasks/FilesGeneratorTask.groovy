package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import groovy.transform.Internal
import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Transformer
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
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
        return outputDir
    }

    @Internal
    Property<MultiFileGenerator.FileNameFormatter> getOutputFormatter() {
        return formatOutput
    }

    FilesGeneratorTask formatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        setFormatOutput(transformer)
        return this
    }

    void setFormatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        formatOutput.set(new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return transformer.transform(t)
            }
        })
    }

    void setFormatOutput(Provider<? extends MultiFileGenerator.FileNameFormatter> provider) {
        formatOutput.set(provider)
    }

    void setFormatOutput(MultiFileGenerator.FileNameFormatter formatter) {
        formatOutput.set(formatter)
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
