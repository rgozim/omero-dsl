package org.openmicroscopy.dsl.tasks

import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class DslBaseTask extends DefaultTask {

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

    void template(Object file) {
        setTemplate(file)
    }

    void setTemplate(Object file) {
        this.template = project.file(file)
    }

    void omeXmlFiles(FileCollection files) {
        setOmeXmlFiles(files)
    }

    void omeXmlFiles(Object... files) {
        setOmeXmlFiles(files)
    }

    void setOmeXmlFiles(FileCollection files) {
        omeXmlFiles = omeXmlFiles + files
    }

    void setOmeXmlFiles(Object... files) {
        omeXmlFiles = project.files(files)
    }

    @TaskAction
    void apply() {
        VelocityEngine ve = new VelocityEngine(velocityProperties)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.omeXmlFiles = omeXmlFiles as Collection
        builder.template = template
        builder.profile = profile
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

}
