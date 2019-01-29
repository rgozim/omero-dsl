package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

@CompileStatic
abstract class DslBaseTask extends DefaultTask {

    private static final def Log = Logging.getLogger(DslBaseTask)

    public static final String OME_XML_EXTENSION = ".ome.xml"

    public static final String DATABASE_TYPES_EXTENSION = "-types.properties"

    @InputFiles
    final ConfigurableFileCollection omeXmlFiles = project.files()

    @InputFiles
    final ConfigurableFileCollection databaseTypes = project.files()

    @Input
    final Property<String> databaseType = project.objects.property(String)

    @InputFile
    final RegularFileProperty template = project.objects.fileProperty()

    @Input
    @Optional
    Properties velocityProperties = new Properties()

    @TaskAction
    void apply() {
        VelocityEngine ve = new VelocityEngine(velocityProperties)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.profile = databaseType.get()
        builder.template = template.get().asFile
        builder.databaseTypes = getDatabaseTypeProperties()
        builder.omeXmlFiles = getOmeXmlFiles()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    //
    // omeXmlFiles
    //

    void omeXmlFiles(TaskProvider task) {
        omeXmlFiles.builtBy(task)
    }

    void omeXmlFiles(Iterable<File> iterable) {
        omeXmlFiles.setFrom(omeXmlFiles + iterable)
    }

    void omeXmlFiles(Object... paths) {
        omeXmlFiles.setFrom(omeXmlFiles + project.files(paths))
    }

    void setOmeXmlFiles(Object... paths) {
        omeXmlFiles.setFrom(paths)
    }

    void setOmeXmlFiles(Iterable<File> paths) {
        omeXmlFiles.setFrom(paths)
    }

    //
    // databaseTypes
    //

    void databaseTypes(TaskProvider task) {
        databaseTypes.builtBy(task)
    }

    void databaseTypes(Iterable<File> iterable) {
        databaseTypes.setFrom(databaseTypes + iterable)
    }

    void databaseTypes(Object... paths) {
        databaseTypes.setFrom(databaseTypes + project.files(paths))
    }

    void setDatabaseTypes(Iterable<File> iterable) {
        databaseTypes.setFrom(iterable)
    }

    void setDatabaseTypes(Object... paths) {
        databaseTypes.setFrom(paths)
    }

    //
    // template
    //

    void template(String dir) {
        setTemplate(dir)
    }

    void setTemplate(String dir) {
        this.template.set(project.file(dir))
    }

    void setTemplate(File dir) {
        this.template.set(dir)
    }

    //
    // databaseType
    //

    void databaseType(String type) {
        setDatabaseType(type)
    }

    void setDatabaseType(String type) {
        this.databaseType.set(type)
    }

    private Properties getDatabaseTypeProperties() {
        Properties databaseTypeProps = new Properties()
        File databaseTypeFile = getDatabaseTypes()
        if (!databaseTypeFile) {
            throw new GradleException("Can't find ${databaseType}${DATABASE_TYPES_EXTENSION}")
        }
        databaseTypeFile.withInputStream { databaseTypeProps.load(it) }
        return databaseTypeProps
    }

    private File getDatabaseTypes() {
        return getFilesInCollection(databaseTypes, DATABASE_TYPES_EXTENSION).find {
            it.name == "${databaseType}${DATABASE_TYPES_EXTENSION}"
        }
    }

    private Collection<File> getOmeXmlFiles() {
        return getFilesInCollection(omeXmlFiles, OME_XML_EXTENSION)
    }

    private Collection<File> getFilesInCollection(FileCollection collection, String extension) {
        def directories = collection.findAll { File file ->
            file.isDirectory()
        }

        def files = collection.findAll { File file ->
            file.isFile() && file.name.endsWith("$extension")
        }

        return files + directories.collectMany {
            project.fileTree(dir: it, include: "**/*$extension").files
        }
    }

}
