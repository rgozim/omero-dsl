package org.openmicroscopy.dsl.tasks

import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class DslBaseTask extends DefaultTask {

    private static final def Log = Logging.getLogger(DslBaseTask)

    @Input
    String profile

    /**
     * The .vm Velocity template file we want to use to generate
     * our sources
     */
    @InputFile
    File template

    @InputFiles
    FileCollection omeXmlFiles = project.files()

    @Input
    @Optional
    Properties velocityProperties = new Properties()

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

    @TaskAction
    void apply() {
        def directories = omeXmlFiles.findAll {
            it.isDirectory()
        }

        def files = omeXmlFiles.findAll {
            it.isFile() && it.name.endsWith(".ome.xml")
        }

        files = files + directories.collectMany {
            project.fileTree(dir: it, include: "**/*.ome.xml").files
        }

        files.each {
            Log.info(it.name)
        }

        VelocityEngine ve = new VelocityEngine(velocityProperties)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.omeXmlFiles = files
        builder.template = template
        builder.profile = profile
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

}
