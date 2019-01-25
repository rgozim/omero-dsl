package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection

class DslExtension {

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    File templatesDir

    File outputDir

    String profile

    DslExtension(Project project) {
        this.project = project
        this.omeXmlFiles = project.files()
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

    void templatesDir(Object path) {
        setTemplatesDir(path)
    }

    void setTemplatesDir(Object path) {
        templatesDir = project.file(path)
    }

    void outputDir(Object path) {
        setOutputDir(path)
    }

    void setOutputDir(Object path) {
        outputDir = project.file(path)
    }

    void profile(String profile) {
        setProfile(profile)
    }

    void setProfile(String t) {
        profile = t
    }
}
