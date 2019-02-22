package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.Factory
import org.openmicroscopy.dsl.FileTypes

import javax.inject.Inject

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
abstract class GeneratorBaseTask extends DefaultTask {

    private static final Logger Log = Logging.getLogger(GeneratorBaseTask)

    private final RegularFileProperty template = project.objects.fileProperty()

    private final RegularFileProperty databaseType = project.objects.fileProperty()

    private final Property<Properties> velocityConfig = project.objects.property(Properties)

    private final ConfigurableFileCollection mappingFiles = project.files()

    private final PatternFilterable omeXmlPatternSet

    GeneratorBaseTask() {
        omeXmlPatternSet = getPatternSetFactory().create()
                .include(FileTypes.PATTERN_OME_XML)
    }

    @Inject
    protected Factory<PatternSet> getPatternSetFactory() {
        throw new UnsupportedOperationException()
    }

    @TaskAction
    void apply() {
        // Create velocity engine with config
        VelocityEngine ve = new VelocityEngine(velocityConfig.get())

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.template = template.get().asFile
        builder.omeXmlFiles = getOmeXmlFiles()
        builder.databaseTypes = getDatabaseTypes()
        builder.profile = getProfile()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    Set<File> getOmeXmlFiles() {
        FileTree src = this.mappingFiles.asFileTree
        return src.matching(omeXmlPatternSet).files
    }

    @Internal
    Properties getDatabaseTypes() {
        Properties databaseTypeProps = new Properties()
        databaseType.get().asFile.withInputStream { databaseTypeProps.load(it) }
        databaseTypeProps
    }

    @Internal
    Provider<String> getProfile() {
        // Determine database type
        databaseType.flatMap { RegularFile file ->
            String fileName = file.asFile.name
            Property<String> p = project.objects.property(String)
            p.set(fileName.substring(0, fileName.lastIndexOf("-")))
            p
        }
    }

    @InputFile
    RegularFileProperty getTemplate() {
        return template
    }

    @InputFile
    RegularFileProperty getDatabaseType() {
        return databaseType
    }

    @Input
    @Optional
    Property<Properties> getVelocityConfig() {
        return velocityConfig
    }

    @Internal
    ConfigurableFileCollection getMappingFiles() {
        return mappingFiles
    }

}
