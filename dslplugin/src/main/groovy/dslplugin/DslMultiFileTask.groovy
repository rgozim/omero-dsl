package dslplugin

import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputDirectory

class DslMultiFileTask extends DslTaskBase {

    @OutputDirectory
    DirectoryProperty outputPath = newOutputDirectory()

    @Input
    Property<MultiFileGenerator.FileNameFormatter> formatOutput =
            project.objects.property(MultiFileGenerator.FileNameFormatter)

    void setOutputPath(File outputPath) {
        this.outputPath.set(outputPath)
    }

    void setFormatOutput(Closure formatOutput) {
        this.formatOutput.set(new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return formatOutput(t)
            }
        })
    }

    @Override
    Generator.Builder createFileGenerator() {
        logger.info("Using MultiFileGenerator")
        logger.info("outputPath set: ${outputPath}")

        def mb = new MultiFileGenerator.Builder()
        mb.outputDir = outputPath.get().asFile
        mb.fileFormatter = formatOutput.get()
        return mb
    }
}
