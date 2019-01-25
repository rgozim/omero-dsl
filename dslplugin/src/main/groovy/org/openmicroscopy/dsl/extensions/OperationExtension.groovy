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

    protected OperationExtension(String name, Project project, String profile) {
        this.name = name
        this.project = project
        this.omeXmlFiles = project.files()
        this.profile = profile
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



