package dslplugin

import ome.dsl.velocity.Generator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputFile

class DslSingleFileTask extends DslTaskBase {

    @OutputFile
    @Optional
    RegularFileProperty outFile = newOutputFile()

    void setOutFile(File outFile) {
        this.outFile.set(outFile)
    }

    @Override
    Generator.Builder createFileGenerator() {
        def b = new SingleFileGenerator.Builder()
        b.outFile = outFile.asFile.get()
        return b
    }
}
