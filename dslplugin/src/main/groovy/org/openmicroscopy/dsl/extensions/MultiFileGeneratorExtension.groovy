package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.provider.Property

@CompileStatic
class MultiFileGeneratorExtension extends OperationExtension {

    final Property<File> outputDir

    final Property<MultiFileGenerator.FileNameFormatter> formatOutput

    MultiFileGeneratorExtension(String name, Project project) {
        super(name, project)
        this.outputDir = project.objects.property(File)
        this.formatOutput = project.objects.property(MultiFileGenerator.FileNameFormatter)
    }

    void outputDir(File dir) {
        setOutputDir(dir)
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        setOutputDir(new File(dir))
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void formatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        setFormatOutput(transformer)
    }

    void setFormatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        formatOutput.set(new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return transformer.transform(t)
            }
        })
    }

}