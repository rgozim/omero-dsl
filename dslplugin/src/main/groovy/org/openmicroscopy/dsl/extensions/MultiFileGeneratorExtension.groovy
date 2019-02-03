package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class MultiFileGeneratorExtension extends OperationExtension {

    File outputDir

    Closure formatOutput

    MultiFileGeneratorExtension(String name, Project project) {
        super(name, project)
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
        this.outputDir = dir
    }

    void formatOutput(Closure closure) {
        setFormatOutput(closure)
    }

    void setFormatOutput(Closure closure) {
        this.formatOutput = closure
    }

}