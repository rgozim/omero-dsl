package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project

@CompileStatic
class SingleFileGeneratorExtension extends OperationExtension {

    File outputFile

    SingleFileGeneratorExtension(String name, Project project) {
        super(name, project)
    }

    void outputFile(String file) {
        setOutputFile(file)
    }

    void outputFile(File file) {
        setOutputFile(file)
    }

    void setOutputFile(String file) {
        setOutputFile(new File(file))
    }

    void setOutputFile(File file) {
        outputFile = file
    }

}

