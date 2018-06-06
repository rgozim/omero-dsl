package dslplugin

import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.*

class DslTask extends DefaultTask {

    @Input
    String profile = "psql"

    @Input
    File template

    @InputFiles
    FileTree omeXmlFiles

    @OutputDirectory
    @Optional
    File outputPath

    @OutputFile
    @Optional
    File outFile

    @Input
    @Optional
    Closure formatOutput

    @Input
    @Optional
    Properties velocityProps

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
