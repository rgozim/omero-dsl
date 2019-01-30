package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider

@CompileStatic
abstract class DslBaseTask extends DefaultTask {

    private static final Logger Log = Logging.getLogger(DslBaseTask)

    public static final String OME_XML_EXTENSION = ".ome.xml"

    public static final String DATABASE_TYPES_EXTENSION = "-types.properties"

    @InputFiles
    FileCollection omeXmlFiles = project.files()

    @InputFiles
    FileCollection databaseTypes = project.files()

    @InputFile
    File template

    @Input
    String databaseType

    @Input
    @Optional
    Properties velocityProperties = new Properties()

    @TaskAction
    void apply() {
        databaseTypes.files.each {
            Log.info("DBTYPE: $it.name")
        }

        VelocityEngine ve = new VelocityEngine(velocityProperties)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.profile = databaseType
        builder.template = template
        builder.databaseTypes = getDatabaseTypeProperties()
        builder.omeXmlFiles = getOmeXmlFiles()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    //
    // omeXmlFiles
    //

    void omeXmlFiles(TaskProvider task) {
        omeXmlFiles = project.files(task)
    }

    void omeXmlFiles(FileCollection files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(FileCollection files) {
        this.omeXmlFiles = files
    }

    //
    // databaseTypes
    //

    void databaseTypes(TaskProvider task) {
        databaseTypes = project.files(task)
    }

    void databaseTypes(FileCollection files) {
        setDatabaseTypes(files)
    }

    void setDatabaseTypes(FileCollection files) {
        this.databaseTypes = files
    }

    //
    // template
    //


    void template(Object dir) {
        setTemplate(dir)
    }

    void setTemplate(Object dir) {
        this.template = project.file(dir)
    }

    //
    // databaseType
    //

    void databaseType(String type) {
        setDatabaseType(type)
    }

    void setDatabaseType(String type) {
        this.databaseType = type
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
