package org.openmicroscopy.dsl.tasks

import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class DslBaseTask extends DefaultTask {

    @Input
    String profile

    @Input
    @Optional
    Properties velocityProperties

    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    FileCollection omeXmlFiles

    /**
     * The .vm Velocity template file we want to use to generate
     * our sources
     */
    @InputFile
    File template

    void setTemplate(File file) {
        this.template = setAbsPath(file)
    }

    void template(File file) {
        setTemplate(file)
    }

    void template(String file) {
        setTemplate(new File(file))
    }

    void setOmeXmlFiles(FileCollection files) {
        if (omeXmlFiles) {
            omeXmlFiles = omeXmlFiles + files
        } else {
            omeXmlFiles = files
        }
    }

    void setOmeXmlFiles(List<File> files) {
        setOmeXmlFiles(project.files(files))
    }

    void omeXmlFiles(FileCollection files) {
        setOmeXmlFiles(files)
    }

    void omeXmlFiles(List<File> files) {
        setOmeXmlFiles(files)
    }

    @TaskAction
    void apply() {
        // Determine which type of file generator to use
        // Create and init velocity engine
        VelocityEngine ve = new VelocityEngine()
        if (velocityProperties) {
            ve.init(velocityProperties)
        } else {
            ve.init()
        }

        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.omeXmlFiles = omeXmlFiles as Collection
        builder.template = template
        builder.profile = profile
        builder.build().call()
    }

    protected File setAbsPath(File file) {
        if (!file.isAbsolute()) {
            return project.file(file)
        } else {
            return file
        }
    }

    abstract protected Generator.Builder createGenerator()
}
