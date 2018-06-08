package dslplugin

import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.SkipWhenEmpty
import org.gradle.api.tasks.TaskAction

class DslTask extends DefaultTask {

    @Input
    String profile = "psql"

    @Input
    File template

    @OutputDirectory
    @Optional
    File outputPath

    @OutputFile
    @Optional
    File outFile

    @Optional
    Closure formatOutput

    @Optional
    Properties velocityProps

    @SkipWhenEmpty
    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    FileCollection omeXmlFiles

    void omeXmlFiles(FileCollection omeXmlFiles) {
        this.omeXmlFiles = this.omeXmlFiles + omeXmlFiles
    }

    @TaskAction
    def apply() {
        if (omeXmlFiles.isEmpty()) {
            throw new GradleException("No .ome.xml files found")
        }

        // Validate
        if (!template || template.isAbsolute()) {
            throw new GradleException("Absolute paths are unsupported for template: ${template}")
        }

        def builder = outputPath != null ? createMultiFileGen() : createSingleFileGen()
        builder.omeXmlFiles = omeXmlFiles as List
        builder.profile = profile
        builder.template = template
        builder.velocityProperties = velocityProps
        builder.build().run()
    }

    MultiFileGenerator.Builder createMultiFileGen() {
        logger.info("Using MultiFileGenerator")
        logger.info("outputPath set: ${outputPath}")

        def mb = new MultiFileGenerator.Builder()
        mb.outputDir = outputPath
        mb.fileFormatter = new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return formatOutput(t)
            }
        }
        return mb
    }

    SingleFileGenerator.Builder createSingleFileGen() {
        logger.info("Using SingleFileGenerator")
        logger.info("outFile set: ${outFile}")

        def b = new SingleFileGenerator.Builder()
        b.outFile = outFile
        return b
    }

}
