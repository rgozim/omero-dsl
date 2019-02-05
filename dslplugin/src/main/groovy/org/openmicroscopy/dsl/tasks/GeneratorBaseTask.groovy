package org.openmicroscopy.dsl.tasks

import groovy.transform.CompileStatic
import groovy.transform.Internal
import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileTree
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet
import org.gradle.internal.Factory
import org.openmicroscopy.dsl.FileTypes

import javax.inject.Inject

@CompileStatic
abstract class GeneratorBaseTask extends DefaultTask {

    private static final Logger Log = Logging.getLogger(GeneratorBaseTask)

    private File template

    private File databaseType

    private final PatternFilterable omeXmlPatternSet

    private List<Object> omeXmlFiles = new ArrayList<>()

    private Properties velocityConfig = new Properties()

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
        VelocityEngine ve = new VelocityEngine(velocityConfig)

        // Build our file generator
        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.profile = databaseType
        builder.template = template
        builder.omeXmlFiles = getOmeXmlFiles().files
        builder.databaseTypes = getDatabaseTypes()
        builder.build().call()
    }

    abstract protected Generator.Builder createGenerator()

    @InputFiles
    @PathSensitive(PathSensitivity.ABSOLUTE)
    FileTree getOmeXmlFiles() {
        ArrayList<Object> copy = new ArrayList<Object>(this.omeXmlFiles)
        FileTree src = project.files(copy).asFileTree
        return src.matching(omeXmlPatternSet)
    }

    @InputFile
    File getDatabaseType() {
        return databaseType
    }

    @Internal
    Properties getDatabaseTypes() {
        Properties databaseTypeProps = new Properties()
        databaseType.withInputStream { databaseTypeProps.load(it) }
        return databaseTypeProps
    }

    @InputFile
    File getTemplate() {
        return template
    }

    @Input
    @Optional
    Properties getVelocityConfig() {
        return velocityConfig
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

    GeneratorBaseTask template(File file) {
        setTemplate(file)
        return this
    }

    void setTemplate(File file) {
        this.template = file
    }

    GeneratorBaseTask databaseType(File file) {
        setDatabaseType(file)
        return this
    }

    void setDatabaseType(File file) {
        this.databaseType = file
    }

    GeneratorBaseTask velocityConfig(Properties config) {
        setVelocityConfig(new Properties(config))
        return this
    }

    void setVelocityConfig(Properties config) {
        this.velocityConfig = config
    }

}
