package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.RegularFileProperty

class OperationExtension {

    final String name

    final Project project

    final RegularFileProperty template

    final ConfigurableFileCollection omeXmlFiles

    OperationExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.template = project.objects.fileProperty()
        this.omeXmlFiles = project.files()
    }

    void omeXmlFiles(Iterable<?> files) {
        setOmeXmlFiles(files)
    }

    void omeXmlFiles(Object... files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(Object... files) {
        omeXmlFiles.setFrom(files)
    }

    void setOmeXmlFiles(Iterable<?> files) {
        omeXmlFiles.setFrom(files)
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
        template.set(t)
    }

}



