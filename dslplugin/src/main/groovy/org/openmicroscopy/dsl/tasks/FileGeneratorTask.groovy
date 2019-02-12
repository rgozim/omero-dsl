package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import ome.dsl.velocity.SingleFileGenerator
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.tasks.OutputFile

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
class FileGeneratorTask extends GeneratorBaseTask {

    /**
     * Set this when you only want to generate a single file
     */
    @OutputFile
    final RegularFileProperty outputFile = project.objects.fileProperty()

    @Override
    protected Generator.Builder createGenerator() {
        return new SingleFileGenerator.Builder()
                .setOutFile(outputFile.get().asFile)
    }

}
