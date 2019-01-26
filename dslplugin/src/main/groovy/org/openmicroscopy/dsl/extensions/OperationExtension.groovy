package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection

class OperationExtension {

    final String name

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    File databaseTypes

    File template

    protected OperationExtension(String name, Project project) {
        this.name = name
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

    void databaseTypes(String databaseTypes) {
        setDatabaseTypes(databaseTypes)
    }

    void databaseTypes(File databaseTypes) {
        setDatabaseTypes(databaseTypes)
    }

    void setDatabaseTypes(String databaseTypes) {
        setDatabaseTypes(new File(databaseTypes))
    }

    void setDatabaseTypes(File databaseTypes) {
        this.databaseTypes = databaseTypes
    }

    void template(String template) {
        setTemplate(template)
    }

    void setTemplate(String t) {
        setTemplate(new File(t))
    }

    void setTemplate(File t) {
        template = t
    }

    void profile(String profile) {
        setProfile(profile)
    }

    void setProfile(String t) {
        profile = t
    }

}



