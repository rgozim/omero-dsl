package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

@CompileStatic
class OperationExtension {

    final String name

    final Project project

    final ConfigurableFileCollection omeXmlFiles

    final Property<File> template

    OperationExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.omeXmlFiles = project.files()
        this.template = project.objects.property(File)
    }

    void omeXmlFiles(Object... files) {
        this.omeXmlFiles.from(files)
    }

    void setOmeXmlFiles(Object... files) {
        this.omeXmlFiles.setFrom(files)
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
        this.template.set(t)
    }

}



