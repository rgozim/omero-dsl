package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import groovy.transform.Internal
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
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
    private final Property<MultiFileGenerator.FileNameFormatter> formatOutput =
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

    @Internal
    Property<MultiFileGenerator.FileNameFormatter> getFormatOutput() {
        return this.formatOutput
    }

}
