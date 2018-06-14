package dslplugin

import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class DslTaskBase extends DefaultTask {

    @InputFile
    final RegularFileProperty template = newInputFile()

    @Input
    final Property<String> profile = project.objects.property(String)

    @Internal
    final Property<Properties> velocityProperties = project.objects.property(Properties)

    @InputFiles
    final ConfigurableFileCollection omeXmlFiles = project.layout.configurableFiles()


    void setTemplate(File template) {
        this.template.set(template)
    }

    void setProfile(String profile) {
        this.profile.set(profile)
    }

    void setOmeXmlFiles(FileCollection omeXmlFiles) {
        this.omeXmlFiles.setFrom(omeXmlFiles)
    }

    void setVelocityProperties(Properties properties) {
        this.velocityProperties.set(properties)
    }

    @TaskAction
    def apply() {
        VelocityEngine ve = new VelocityEngine()
        ve.init(velocityProperties.get())

        def builder = createFileGenerator()
        builder.template = template.asFile.get()
        builder.omeXmlFiles = omeXmlFiles.getFiles()
        builder.profile = profile.get()
        builder.velocityEngine = ve
        builder.build().run()
    }

    abstract Generator.Builder createFileGenerator()
}
