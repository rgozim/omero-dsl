package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.AbstractCopyTask

class DslExtension {
    final Project project

    FileCollection omeXmlFiles

    FileCollection templateFiles

    File outputDir

    void templateFiles(FileCollection files) {
        if (templateFiles) {
            templateFiles = templateFiles + files
        } else {
            templateFiles = files
        }
    }

    void templateFiles(Object... files) {
        setTemplateFiles(files)
    }

    void setTemplateFiles(Object... files) {
        templateFiles = project.files(files)
    }

    void omeXmlFiles(FileCollection files) {
        if (omeXmlFiles) {
            omeXmlFiles = omeXmlFiles + files
        } else {
            omeXmlFiles = files
        }
        AbstractCopyTask
    }

    void omeXmlFiles(Object... files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(Object... files) {
        omeXmlFiles = project.files(files)
    }

    void outputDir(Object path) {
        setOutputDir(path)
    }

    void setOutputDir(Object path) {
        outputDir = project.file(path)
    }

    DslExtension(Project project) {
        this.project = project
    }
}
