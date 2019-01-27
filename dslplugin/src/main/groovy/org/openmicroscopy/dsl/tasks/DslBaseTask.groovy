package org.openmicroscopy.dsl.tasks

import ome.dsl.velocity.Generator
import org.apache.commons.lang3.StringUtils
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class DslBaseTask extends DefaultTask {

    private static final def Log = Logging.getLogger(DslBaseTask)

    @InputFile
    File databaseTypes

    @InputFile
    File template

    @InputFiles
    FileCollection omeXmlFiles = project.files()

    @Input
    @Optional
    Properties velocityProperties = new Properties()

    abstract protected Generator.Builder createGenerator()

    @TaskAction
    void apply() {
        Log.info("Template : $template.name")
        Log.info("DatabaseTypesFile : $databaseTypes.name")
        Log.info("Profile : ${StringUtils.substringBefore(databaseTypes.name, '-')}")

        VelocityEngine ve = new VelocityEngine(velocityProperties)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.profile = profile
        builder.databaseTypes = databaseTypeProperties
        builder.omeXmlFiles = allOmeXmlFiles
        builder.template = template
        builder.build().call()
    }

    File template(Object file) {
        return setTemplate(file)
    }

    void setTemplate(Object file) {
        this.template = project.file(file)
    }

    FileCollection omeXmlFiles(FileCollection files) {
        return omeXmlFiles = omeXmlFiles + files
    }

    FileCollection omeXmlFiles(Object... files) {
        return omeXmlFiles(project.files(files))
    }

    void setOmeXmlFiles(FileCollection files) {
        omeXmlFiles = files
    }

    void setOmeXmlFiles(Object... files) {
        omeXmlFiles = project.files(files)
    }

    /**
     * Temp undecided method that reads the profile from the file name of
     * @code databaseTypes*
     *
     * Format example : psql-types.properties
     *
     * @return profile portion of filename
     */
    String getProfile() {
        return StringUtils.substringBefore(databaseTypes.name, '-')
    }

    Properties getDatabaseTypeProperties() {
        Properties databaseTypeProps = new Properties()
        databaseTypes.withInputStream { databaseTypeProps.load(it) }
        return databaseTypeProps
    }

    List<File> getAllOmeXmlFiles() {
        def directories = omeXmlFiles.findAll {
            it.isDirectory()
        }

        def files = omeXmlFiles.findAll {
            it.isFile() && it.name.endsWith(".ome.xml")
        }

        files = files + directories.collectMany {
            project.fileTree(dir: it, include: "**/*.ome.xml").files
        }

        return files
    }

}
