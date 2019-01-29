package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.tasks.Nested
import org.gradle.api.tasks.OutputDirectory

@CompileStatic
class DslMultiFileTask extends DslBaseTask {

    /**
     * Set this when you want to generate multiple files
     * Note: also requires setting {@link this.formatOutput}
     */
    @OutputDirectory
    final DirectoryProperty outputDir = project.objects.directoryProperty()

    @Nested
    MultiFileGenerator.FileNameFormatter formatOutput

    void formatOutput(Closure<String> closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(Closure<String> closure) {
        formatOutput = new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return closure(t)
            }
        }
    }

    void formatOutput(Transformer<String, SemanticType> transformer) {
        setFormatOutput(transformer)
    }

    void setFormatOutput(Transformer<String, SemanticType> transformer) {
        formatOutput = { SemanticType t ->
            return transformer.transform(t)
        }
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        this.outputDir.set(project.file(dir))
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    @Override
    protected Generator.Builder createGenerator() {
        return new MultiFileGenerator.Builder()
                .setOutputDir(outputDir.get().asFile)
                .setFileNameFormatter(formatOutput)
    }

}
