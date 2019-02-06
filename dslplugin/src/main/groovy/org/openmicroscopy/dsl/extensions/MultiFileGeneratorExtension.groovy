package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Property

@CompileStatic
class MultiFileGeneratorExtension extends OperationExtension {

    final Property<File> outputDir

    Closure formatOutput

    MultiFileGeneratorExtension(String name, Project project) {
        super(name, project)
        this.outputDir = project.objects.property(File)
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

    void formatOutput(Closure closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(Closure closure) {
        this.formatOutput = closure
    }

}