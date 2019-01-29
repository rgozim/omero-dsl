package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property

class DslExtension {

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    final ConfigurableFileCollection databaseTypes

    final ConfigurableFileCollection templates

    final Property<String> databaseType

    final DirectoryProperty outputDir

    DslExtension(Project project) {
        this.project = project
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
        this.databaseType = project.objects.property(String)
        this.outputDir = project.objects.directoryProperty()
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

    void databaseTypes(FileCollection files) {
        setDatabaseTypes(files)
    }

    void databaseTypes(Object... files) {
        setDatabaseTypes(files)
    }

    void setDatabaseTypes(Object... files) {
        databaseTypes.setFrom(files)
    }

    void setDatabaseTypes(FileCollection files) {
        databaseTypes.setFrom(files)
    }

    void databaseType(String type) {
        databaseType.set(type)
    }

    void outputDir(Object path) {
        setOutputDir(path)
    }

    void setOutputDir(Object path) {
        outputDir.set(project.file(path))
    }

}
