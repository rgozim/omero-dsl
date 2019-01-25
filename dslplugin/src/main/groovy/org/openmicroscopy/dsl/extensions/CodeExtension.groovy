package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project

class CodeExtension extends OperationExtension {

    File outputDir

    Closure formatOutput

    CodeExtension(String name, Project project, String profile) {
        super(name, project, profile)
    }

    void outputDir(File dir) {
        this.outputPath = dir
    }

    void outputDir(String dir) {
        setOutputDir(dir)
    }

    void setOutputDir(String dir) {
        outputDir = new File(dir)
    }

    void formatOutput(Closure closure) {
        this.formatOutput = closure
    }
}