package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection

class OperationExtension {

    final String name

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    String profile

    File template

    protected OperationExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.omeXmlFiles = project.files()
        this.profile = "psql"
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

    void template(Object template) {
        setTemplate(template)
    }

    void setTemplate(Object t) {
        template = project.file(t)
    }

}



