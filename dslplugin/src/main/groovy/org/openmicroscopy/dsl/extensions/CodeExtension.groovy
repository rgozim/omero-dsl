package org.openmicroscopy.dsl.extensions

import ome.dsl.SemanticType
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

class CodeExtension extends OperationExtension {

    final DirectoryProperty outputDir

    Closure formatOutput

    CodeExtension(String name, Project project) {
        super(name, project)
        outputDir = project.objects.directoryProperty()
    }

    void outputDir(File dir) {
        this.outputDir.set(dir)
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        outputDir.set(new File(dir))
    }

    void formatOutput(Closure<SemanticType> closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(Closure<SemanticType> closure) {
        this.formatOutput = closure
    }

}