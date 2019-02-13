package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Provider

import static org.openmicroscopy.dsl.FileTypes.PATTERN_DB_TYPE
import static org.openmicroscopy.dsl.FileTypes.PATTERN_OME_XML
import static org.openmicroscopy.dsl.FileTypes.PATTERN_TEMPLATE

@CompileStatic
class DslExtension {

    private final Project project

    final String name

    final VelocityConfig velocity = new VelocityConfig()

    final NamedDomainObjectContainer<MultiFileConfig> multiFile

    final NamedDomainObjectContainer<SingleFileConfig> singleFile

    final ConfigurableFileCollection omeXmlFiles

    final ConfigurableFileCollection databaseTypes

    final ConfigurableFileCollection templates

    final DirectoryProperty outputDir

    DslExtension(String name,
                 Project project,
                 NamedDomainObjectContainer<MultiFileConfig> multiFile,
                 NamedDomainObjectContainer<SingleFileConfig> singleFile) {
        this.name = name
        this.project = project
        this.multiFile = multiFile
        this.singleFile = singleFile
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
        this.outputDir = project.objects.directoryProperty()

        // Set some conventions
        this.outputDir.convention(project.layout.projectDirectory.dir("src/psql"))
        this.omeXmlFiles.setFrom(project.fileTree(dir: "src/main/resources/mappings", include: PATTERN_OME_XML))
        this.databaseTypes.setFrom(project.fileTree(dir: "src/main/resources/properties", include: PATTERN_DB_TYPE))
        this.templates.setFrom(project.fileTree(dir: "src/main/resources/templates", include: PATTERN_TEMPLATE))
    }

    void multiFile(Action<? super NamedDomainObjectContainer<MultiFileConfig>> action) {
        action.execute(this.multiFile)
    }

    void singleFile(Action<? super NamedDomainObjectContainer<SingleFileConfig>> action) {
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

    void velocityConfig(Action<? super VelocityConfig> action) {
        setTemplates(action)
    }

    void setTemplates(Action<? super VelocityConfig> action) {
        action.execute(velocity)
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
