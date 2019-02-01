package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
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
abstract class GeneratorBaseTask extends DefaultTask {

    private static final Logger Log = Logging.getLogger(GeneratorBaseTask)

    @InputFiles
    FileCollection omeXmlFiles = project.files()

    @InputFile
    File template

    @InputFile
    File databaseType     // Could be a directory or a file

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

    void template(Object dir) {
        setTemplate(dir)
    }

    void setTemplate(Object dir) {
        this.template = project.file(dir)
    }

    void databaseType(Object file) {
        setDatabaseType(file)
    }

    void setDatabaseType(Object file) {
        this.databaseType = project.file(file)
    }

    private Properties _getDatabaseTypeProperties() {
        Properties databaseTypeProps = new Properties()
        databaseType.withInputStream { databaseTypeProps.load(it) }
        return databaseTypeProps
    }

    private Collection<File> _getOmeXmlFiles() {
        return _getFilesInCollection(omeXmlFiles, FileTypes.PATTERN_OME_XML)
    }

    private static Collection<File> _getFilesInCollection(FileCollection collection, String include) {
        PatternSet patternSet = new PatternSet().include(include)
        return collection.asFileTree.matching(patternSet).files
    }

}
