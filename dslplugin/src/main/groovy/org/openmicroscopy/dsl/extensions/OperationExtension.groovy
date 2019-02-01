package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.openmicroscopy.dsl.extensions.specs.OperationSpec

@CompileStatic
class OperationExtension implements OperationSpec {

    final String name

    final Project project

    FileCollection omeXmlFiles

    File template

    OperationExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.omeXmlFiles = project.files()
    }

    void omeXmlFiles(Object... files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(Object... files) {
        this.omeXmlFiles = project.files(files)
    }

    void template(String template) {
        setTemplate(template)
    }

    void template(File template) {
        setTemplate(template)
    }

    void setTemplate(String t) {
        setTemplate(new File(t))
    }

    void setTemplate(File t) {
        this.template = t
    }

}



