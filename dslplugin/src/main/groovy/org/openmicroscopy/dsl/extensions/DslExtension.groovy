package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class DslExtension {
    final Project project

    FileCollection omeXmlFiles

    FileCollection templateFiles

    File outputPath

    void templateFiles(FileCollection files) {
        if (templateFiles) {
            templateFiles = templateFiles + files
        } else {
            templateFiles = files
        }
    }

    void omeXmlFiles(FileCollection files) {
        if (omeXmlFiles) {
            omeXmlFiles = omeXmlFiles + files
        } else {
            omeXmlFiles = files
        }
    }

    void setOmeXmlFiles(List<File> files) {
        this.omeXmlFiles = project.files(files)
    }

    void omeXmlFiles(List<File> files) {
        setOmeXmlFiles(files)
    }

    void setOutputPath(File path) {
        if (!path.isAbsolute()) {
            outputPath = project.file(path)
        } else {
            outputPath = path
        }
    }

    void outputPath(String path) {
        setOutputPath(new File(path))
    }

    void outputPath(File path) {
        setOutputPath(path)
    }

    DslExtension(Project project) {
        this.project = project
    }
}
