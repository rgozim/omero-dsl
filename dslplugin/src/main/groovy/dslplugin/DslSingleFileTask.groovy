package dslplugin

import ome.dsl.velocity.Generator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

class DslSingleFileTask extends DslTaskBase {

    @OutputFile
    @Optional
    RegularFileProperty outFile = project.layout.fileProperty()

    void setOutFile(File outFile) {
        this.outFile.set(outFile)
    }

    @Override
    Generator.Builder createFileGenerator() {
        logger.info("Using SingleFileGenerator")
        logger.info("outFile set: ${outFile}")

        def b = new SingleFileGenerator.Builder()
        b.outFile = outFile.get().asFile
        return b
    }
}
