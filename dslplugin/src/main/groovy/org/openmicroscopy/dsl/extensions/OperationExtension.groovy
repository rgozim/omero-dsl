package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.FileCollection

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

    void setTemplate(String t) {
        template = new File(t)
    }

    void template(String t) {
        setTemplate(t)
    }

    void template(File template) {
        this.template = template
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
        this.omeXmlFiles(project.files(files))
    }
}



