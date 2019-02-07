package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.openmicroscopy.dsl.extensions.specs.DslSpec

@CompileStatic
class DslExtension implements DslSpec {

    private final Project project

    final NamedDomainObjectContainer<MultiFileGeneratorExtension> multiFile

    final NamedDomainObjectContainer<SingleFileGeneratorExtension> singleFile

    final ConfigurableFileCollection omeXmlFiles

    final ConfigurableFileCollection databaseTypes

    final ConfigurableFileCollection templates

    final DirectoryProperty outputDir

    final Property<String> database

    DslExtension(Project project,
                 NamedDomainObjectContainer<MultiFileGeneratorExtension> multiFile,
                 NamedDomainObjectContainer<SingleFileGeneratorExtension> singleFile) {
        this.project = project
        this.multiFile = multiFile
        this.singleFile = singleFile
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
        this.outputDir = project.objects.directoryProperty()
        this.database = project.objects.property(String)
    }

    void multiFile(Action<? super NamedDomainObjectContainer<MultiFileGeneratorExtension>> action) {
        action.execute(this.multiFile)
    }

    void singleFile(Action<? super NamedDomainObjectContainer<SingleFileGeneratorExtension>> action) {
        action.execute(this.singleFile)
    }

    void omeXmlFiles(FileCollection files) {
        this.omeXmlFiles.from files
    }

    void setOmeXmlFiles(FileCollection files) {
        this.omeXmlFiles.setFrom(files)
    }

    void databaseTypes(FileCollection files) {
        this.databaseTypes.from files
    }

    void setDatabaseTypes(FileCollection files) {
        this.databaseTypes.setFrom files
    }

    void templates(FileCollection files) {
        this.templates.from files
    }

    void setTemplates(FileCollection files) {
        this.templates.setFrom files
    }

    void setOutputDir(Provider<? extends Directory> dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(Directory dir) {
        this.outputDir.set(dir)
    }

    void setOutputDir(File dir) {
        this.outputDir.set(dir)
    }

    void database(String db) {
        this.database.set(db)
    }

}
