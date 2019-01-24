package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection

class DslExtension {

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    final ConfigurableFileCollection templateFiles

    File outputDir

    DslExtension(Project project) {
        this.project = project
        this.omeXmlFiles = project.files()
        this.templateFiles = project.files()
    }

    void templateFiles(FileCollection files) {
        templateFiles.setFrom(templateFiles + files)
    }

    void templateFiles(Object... files) {
        templateFiles(project.files(files))
    }

    void setTemplateFiles(FileCollection files) {
        templateFiles.setFrom(files)
    }

    void setTemplateFiles(Object... files) {
        setTemplateFiles(project.files(files))
    }

    void omeXmlFiles(FileCollection files) {
        omeXmlFiles.setFrom(omeXmlFiles + files)
    }

    void omeXmlFiles(Object... files) {
        omeXmlFiles(project.files(files))
    }

    void setOmeXmlFiles(FileCollection files) {
        omeXmlFiles.setFrom(files)
    }

    void setOmeXmlFiles(Object... files) {
        setOmeXmlFiles(project.files(files))
    }

    void outputDir(Object path) {
        setOutputDir(path)
    }

    void setOutputDir(Object path) {
        outputDir = project.file(path)
    }

}
