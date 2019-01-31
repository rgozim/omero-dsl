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
        VelocityEngine ve = new VelocityEngine(velocityProperties)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.profile = databaseType
        builder.template = template
        builder.databaseTypes = _getDatabaseTypeProperties()
        builder.omeXmlFiles = _getOmeXmlFiles()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    void omeXmlFiles(Object... files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(Object... files) {
        this.omeXmlFiles = project.files(files)
    }

    void databaseTypes(Object... files) {
        setDatabaseTypes(files)
    }

    void setDatabaseTypes(Object... files) {
        this.databaseTypes = project.files(files)
    }

    void template(Object dir) {
        setTemplate(dir)
    }

    void setTemplate(Object dir) {
        this.template = project.file(dir)
    }

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
        return _getFilesInCollection(databaseTypes, FileTypes.PATTERN_DB_TYPE).find { File file ->
            file.name.contains(databaseType)
        }
    }

    private Collection<File> _getOmeXmlFiles() {
        return _getFilesInCollection(omeXmlFiles, FileTypes.PATTERN_OME_XML)
    }

    private static Collection<File> _getFilesInCollection(FileCollection collection, String include) {
        PatternSet patternSet = new PatternSet().include(include)
        return collection.asFileTree.matching(patternSet).files
    }

}
