package org.openmicroscopy.dsl.extensions


import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty

class ResourceExtension extends OperationExtension {

    final RegularFileProperty outputFile

    ResourceExtension(String name, Project project) {
        super(name, project)
        outputFile = project.objects.fileProperty()
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

