package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.provider.Property

@CompileStatic
class SingleFileGeneratorExtension extends OperationExtension {

    final Property<File> outputFile

    SingleFileGeneratorExtension(String name, Project project) {
        super(name, project)
        outputFile = project.objects.property(File)
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
        outputFile.set(file)
    }

}

