package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.FileCollection

@CompileStatic
class DslExtension {

    final Project project

    final NamedDomainObjectContainer<CodeExtension> code

    final NamedDomainObjectContainer<ResourceExtension> resource

    FileCollection omeXmlFiles

    FileCollection databaseTypes

    FileCollection templates

    String databaseType

    File outputDir

    DslExtension(Project project, NamedDomainObjectContainer<CodeExtension> code,
                 NamedDomainObjectContainer<ResourceExtension> resource) {
        this.project = project
        this.code = code
        this.resource = resource
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
    }

    void code(Action<? super NamedDomainObjectContainer<CodeExtension>> action) {
        action.execute(code)
    }

    void resource(Action<? super NamedDomainObjectContainer<ResourceExtension>> action) {
        action.execute(resource)
    }

    void omeXmlFiles(FileCollection files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(FileCollection files) {
        // this.omeXmlFiles.setFrom(files)
        this.omeXmlFiles = files
    }

    void templates(FileCollection files) {
        setTemplates(files)
    }

    void setTemplates(FileCollection files) {
        // this.templates.setFrom(files)
        this.templates = files
    }

    void databaseTypes(FileCollection files) {
        setDatabaseTypes(files)
    }

    void setDatabaseTypes(FileCollection files) {
        // this.databaseTypes.setFrom(files)
        this.databaseTypes = files
    }

    void databaseType(String type) {
        setDatabaseType(type)
    }

    void setDatabaseType(String type) {
        this.databaseType = type
    }

    void outputDir(String path) {
        setOutputDir(path)
    }

    void outputDir(File path) {
        setOutputDir(path)
    }

    void setOutputDir(String path) {
        this.outputDir = project.file(path)
    }

    void setOutputDir(File path) {
        this.outputDir = path
    }

}
