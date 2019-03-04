package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Provider

import static org.openmicroscopy.dsl.FileTypes.PATTERN_DB_TYPE
import static org.openmicroscopy.dsl.FileTypes.PATTERN_OME_XML
import static org.openmicroscopy.dsl.FileTypes.PATTERN_TEMPLATE

@CompileStatic
class DslExtension {

    private final Project project

    final VelocityConfig velocity = new VelocityConfig()

    final NamedDomainObjectContainer<SingleFileConfig> singleFile

    final NamedDomainObjectContainer<MultiFileConfig> multiFile

    final ConfigurableFileCollection omeXmlFiles

    final ConfigurableFileCollection databaseTypes

    final ConfigurableFileCollection templates

    final DirectoryProperty outputDir

    final ListProperty<String> databases

    DslExtension(Project project,
                 NamedDomainObjectContainer<SingleFileConfig> singleFile,
                 NamedDomainObjectContainer<MultiFileConfig> multiFile
    ) {
        this.project = project
        this.singleFile = singleFile
        this.multiFile = multiFile
        this.omeXmlFiles = project.files()
        this.databaseTypes = project.files()
        this.templates = project.files()
        this.outputDir = project.objects.directoryProperty()
        this.databases = project.objects.listProperty(String)

        // Set some conventions
        this.databases.convention(["psql"])
        this.outputDir.convention(project.layout.projectDirectory.dir("src/generated"))
        this.omeXmlFiles.setFrom(project.fileTree(dir: "src/main/resources/mappings", include: PATTERN_OME_XML))
        this.databaseTypes.setFrom(project.fileTree(dir: "src/main/resources/properties", include: PATTERN_DB_TYPE))
        this.templates.setFrom(project.fileTree(dir: "src/main/resources/templates", include: PATTERN_TEMPLATE))
    }

    void singleFile(Action<? super NamedDomainObjectContainer<SingleFileConfig>> action) {
        action.execute(this.singleFile)
    }

    void multiFile(Action<? super NamedDomainObjectContainer<MultiFileConfig>> action) {
        action.execute(this.multiFile)
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

    void databases(String... databases) {
        this.databases.addAll(databases)
    }

    void setDatabases(Iterable<String> databases) {
        this.databases.set(databases)
    }

}
