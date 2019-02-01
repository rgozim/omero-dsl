package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.openmicroscopy.dsl.extensions.specs.DslSpec

@CompileStatic
class DslExtension implements DslSpec {

    final Project project

    final NamedDomainObjectContainer<MultiFileGeneratorExtension> multiFile

    final NamedDomainObjectContainer<SingleFileGeneratorExtension> singleFile

    FileCollection omeXmlFiles

    FileCollection databaseTypes

    FileCollection templates

    File outputDir

    String database

    DslExtension(Project project,
                 NamedDomainObjectContainer<MultiFileGeneratorExtension> multiFile,
                 NamedDomainObjectContainer<SingleFileGeneratorExtension> singleFile) {
        this.project = project
        this.multiFile = multiFile
        this.singleFile = singleFile
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
    }

    void multiFile(Action<? super NamedDomainObjectContainer<MultiFileGeneratorExtension>> action) {
        action.execute(multiFile)
    }

    void singleFile(Action<? super NamedDomainObjectContainer<SingleFileGeneratorExtension>> action) {
        action.execute(singleFile)
    }

    void omeXmlFiles(FileCollection files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(FileCollection files) {
        this.omeXmlFiles = files
    }

    void templates(FileCollection files) {
        setTemplates(files)
    }

    void setTemplates(FileCollection files) {
        this.templates = files
    }

    void databaseTypes(FileCollection files) {
        setDatabaseTypes(files)
    }

    void setDatabaseTypes(FileCollection files) {
        this.databaseTypes = files
    }

    void outputDir(Object dir) {
        setOutputDir(dir)
    }

    void setOutputDir(Object dir) {
        this.outputDir = project.file(dir)
    }

}
