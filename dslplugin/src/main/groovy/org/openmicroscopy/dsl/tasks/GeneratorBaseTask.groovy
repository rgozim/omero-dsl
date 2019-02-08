package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.Transformer
import org.gradle.api.file.FileTree
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.model.ObjectFactory
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

    private final List<Object> omeXmlFiles = new ArrayList<>()

    private final PatternFilterable omeXmlPatternSet

    private final Property<Properties> velocityConfig = project.objects.property(Properties)


    GeneratorBaseTask() {
        omeXmlPatternSet = getPatternSetFactory().create()
                .include(FileTypes.PATTERN_OME_XML)
    }

    @Inject
    protected Factory<PatternSet> getPatternSetFactory() {
        throw new UnsupportedOperationException()
    }

    @Inject
    protected ObjectFactory getObjectFactory() {
        throw new UnsupportedOperationException()
    }

    @Inject
    protected ProjectLayout getProjectLayout() {
        throw new UnsupportedOperationException()
    }

    @TaskAction
    void apply() {
        // Create velocity engine with config
        VelocityEngine ve = new VelocityEngine(
                velocityConfig.getOrElse(new Properties()))

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.template = template.get().asFile
        builder.omeXmlFiles = getOmeXmlFiles().files
        builder.databaseTypes = getDatabaseTypes()
        builder.profile = getProfile()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    @InputFiles
    @PathSensitive(PathSensitivity.RELATIVE)
    FileTree getOmeXmlFiles() {
        ArrayList<Object> copy = new ArrayList<Object>(this.omeXmlFiles)
        FileTree src = project.files(copy).asFileTree
        return src.matching(omeXmlPatternSet)
    }

    @InputFile
    RegularFileProperty getDatabaseType() {
        return databaseType
    }

    @InputFile
    RegularFileProperty getTemplate() {
        return template
    }

    @Input
    @Optional
    Property<Properties> getVelocityConfig() {
        return velocityConfig
    }

    @Internal
    Properties getDatabaseTypes() {
        Properties databaseTypeProps = new Properties()
        databaseType.get().asFile.withInputStream { databaseTypeProps.load(it) }
        return databaseTypeProps
    }

    @Internal
    Provider<String> getProfile() {
        // Determine database type
        return databaseType.flatMap(new Transformer<Provider<String>, RegularFile>() {
            @Override
            Provider<String> transform(RegularFile t) {
                String fileName = t.asFile.name
                Property<String> p = project.objects.property(String)
                p.set(fileName.substring(0, fileName.lastIndexOf("-")))
                p
            }
        })
    }

    GeneratorBaseTask omeXmlFiles(Object... omeXml) {
        Collections.addAll(this.omeXmlFiles, omeXml)
        return this
    }

    void setOmeXmlFiles(FileTree source) {
        setOmeXmlFiles((Object) source)
    }

    void setOmeXmlFiles(Object... omeXml) {
        this.omeXmlFiles.clear()
        this.omeXmlFiles.add(omeXml)
    }

    void setVelocityConfig(Provider<Properties> config) {
        this.velocityConfig.set(config)
    }

    void setVelocityConfig(Properties config) {
        this.velocityConfig.set(config)
    }

    void setTemplate(Provider<? extends RegularFile> template) {
        this.template.set(template)
    }

    void setTemplate(File template) {
        this.template.set(template)
    }

    void setDatabaseType(Provider<? extends RegularFile> template) {
        this.databaseType.set(template)
    }

    void setDatabaseType(File template) {
        this.databaseType.set(template)
    }

}
