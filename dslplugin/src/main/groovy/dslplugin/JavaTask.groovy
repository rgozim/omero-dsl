package dslplugin

import ome.dsl.velocity.JavaGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.TaskAction

class JavaTask extends DefaultTask {

    @Input
    String profile = "psql"

    @InputFile
    File template

    @InputFiles
    FileTree omeXmlFiles

    @OutputDirectory
    File outputPath

    Properties velocityProps

    @TaskAction
    def apply() {
        def generator = new JavaGenerator.Builder()
                .setProfile(profile)
                .setOmeXmlFiles(omeXmlFiles as List)
                .setTemplate(template)
                .setOutputDir(outputPath)
                .setVelocityProperties(velocityProps)
                .build()

        generator.run()
    }

}
