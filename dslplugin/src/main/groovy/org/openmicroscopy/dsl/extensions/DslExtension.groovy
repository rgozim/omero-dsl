package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection

class DslExtension {

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    FileCollection templates

    File outputDir

    File databaseTypes

    DslExtension(Project project) {
        this.project = project
        this.omeXmlFiles = project.files()
        this.templates = project.files()
    }

    void omeXmlFiles(FileCollection files) {
        setOmeXmlFiles(files)
    }

    void omeXmlFiles(Object... files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(FileCollection files) {
        omeXmlFiles.setFrom(files)
    }

    void setOmeXmlFiles(Object... files) {
        setOmeXmlFiles(project.files(files))
    }

    void templates(FileCollection files) {
        setTemplates(files)
    }

    void templates(Object... files) {
        setTemplates(files)
    }

    void setTemplates(Object... files) {
        setTemplates(project.files(files))
    }

    void setTemplates(FileCollection files) {
        templates.setFrom(files)
    }

    void outputDir(Object path) {
        setOutputDir(path)
    }

    void setOutputDir(Object path) {
        outputDir = project.file(path)
    }

    void databaseTypes(Object dir) {
        setDatabaseTypes(dir)
    }

    void setDatabaseTypes(Object dir) {
        databaseTypes = project.file(dir)
    }

}
