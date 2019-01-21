package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.AbstractCopyTask

class OperationExtension {

    public final String name

    public final Project project

    String profile

    File template

    FileCollection omeXmlFiles

    protected OperationExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.profile = "psql"
    }

    void template(Object template) {
        setTemplate(template)
    }

    void setTemplate(Object t) {
        template = project.file(t)
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

}



