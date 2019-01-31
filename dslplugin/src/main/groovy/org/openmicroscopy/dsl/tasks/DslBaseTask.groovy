package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Task
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.api.tasks.util.PatternSet
import org.openmicroscopy.dsl.FileTypes

@CompileStatic
abstract class DslBaseTask extends DefaultTask {

    private static final Logger Log = Logging.getLogger(DslBaseTask)

    @InputFiles
    FileCollection omeXmlFiles = project.files()

    @InputFiles
    @Optional
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
        omeXmlFiles.asFileTree.files.each {
            Log.info("OMEXML $it")
        }

        databaseTypes.asFileTree.files.each {
            Log.info("DBTYPEFILE $it")
        }

//        VelocityEngine ve = new VelocityEngine(velocityProperties)

//        // Build our file generator
//        def builder = createGenerator()
//        builder.velocityEngine = ve
//        builder.profile = databaseType
//        builder.template = template
//        builder.databaseTypes = _getDatabaseTypeProperties()
//        builder.omeXmlFiles = _getOmeXmlFiles()
//        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    //
    // omeXmlFiles
    //

//    void omeXmlFiles(Iterable<?> files) {
//        setOmeXmlFiles(files)
//    }
//
//    void omeXmlFiles(Object... files) {
//        setOmeXmlFiles(files)
//    }
//
//    void setOmeXmlFiles(Iterable<?> files) {
//        this.omeXmlFiles.setFrom(files) //= project.files(files)
//    }
//
//    void setOmeXmlFiles(Object... files) {
//        this.omeXmlFiles.setFrom(files)
//    }

    //
    // databaseTypes
    //


//    void databaseTypes(Iterable<?> files) {
//        setDatabaseTypes(files)
//    }
//
//    void databaseTypes(Object... files) {
//        setDatabaseTypes(files)
//    }
//
//    void setDatabaseTypes(Iterable<?> files) {
//        this.databaseTypes.setFrom(files)
//    }
//
//    void setDatabaseTypes(Object... files) {
//        this.databaseTypes.setFrom(files)
//    }

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

    private Properties _getDatabaseTypeProperties() {
        Properties databaseTypeProps = new Properties()
        File databaseTypeFile = _getDatabaseTypesFile()
        if (!databaseTypeFile) {
            throw new GradleException("Can't find ${databaseType}-type.${FileTypes.EXTENSION_DB_TYPE}")
        }
        databaseTypeFile.withInputStream { databaseTypeProps.load(it) }
        return databaseTypeProps
    }

    private File _getDatabaseTypesFile() {
        def dbtypefiles = _getFilesInCollection(databaseTypes, "properties")
        return dbtypefiles.find {
            it.name == "${databaseType}-type.${FileTypes.EXTENSION_DB_TYPE}"
        }
    }

    private Collection<File> _getOmeXmlFiles() {
        return _getFilesInCollection(omeXmlFiles, FileTypes.EXTENSION_OME_XML)
    }

    private static Collection<File> _getFilesInCollection(FileCollection collection, String extension) {
        def directories = collection.findAll { File file ->
            file.isDirectory()
        }

        def files = collection.findAll { File file ->
            file.isFile() && file.name.matches("$extension")
        }

        return files + directories.collectMany {
            project.fileTree(dir: it, include: "**/*.$extension").files
        }
    }

}
