package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Project
import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.openmicroscopy.dsl.utils.SemanticTypeClosure
import org.openmicroscopy.dsl.utils.SemanticTypeTransformer

@CompileStatic
class MultiFileConfig extends BaseFileConfig {

    final DirectoryProperty outputDir

    final Property<MultiFileGenerator.FileNameFormatter> formatOutput

    MultiFileConfig(String name, Project project) {
        super(name, project)
        this.outputDir = project.objects.directoryProperty()
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

    void formatOutput(Closure closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(final Transformer<? extends String, ? super SemanticType> transformer) {
        formatOutput.set(new SemanticTypeTransformer(transformer))
    }

    void setFormatOutput(Closure closure) {
        formatOutput.set(new SemanticTypeClosure(closure))
    }

}