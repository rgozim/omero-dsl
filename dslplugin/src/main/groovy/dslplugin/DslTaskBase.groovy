package dslplugin


import ome.dsl.velocity.Generator
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.TaskAction

abstract class DslTaskBase extends DefaultTask {

    @Input
    Property<String> profile = project.objects.property(String)

    @Input
    RegularFileProperty template = project.layout.fileProperty()

    @InputFiles
    ConfigurableFileCollection omeXmlFiles = project.layout.configurableFiles()

    @Input
    @Optional
    Property<Properties> velocityProps = project.objects.property(Properties)

    void setProfile(String profile) {
        this.profile.set profile
    }

    void setTemplate(File template) {
        this.template.set template
    }

    void setOmeXmlFiles(FileCollection omeXmlFiles) {
        this.omeXmlFiles.setFrom omeXmlFiles
    }

    void setVelocityProps(Properties velocityProps) {
        this.velocityProps.set velocityProps
    }

    @TaskAction
    def apply() {
        // Validate
        if (!template || template.isAbsolute()) {
            throw new GradleException("Absolute paths are unsupported for template: ${template}")
        }

        def builder = createFileGenerator()
        builder.omeXmlFiles = omeXmlFiles.getFiles()
        builder.template = template.asFile.get()
        builder.profile = profile.get()
        builder.velocityProperties = velocityProps.get()
        builder.build().run()
    }

    abstract Generator.Builder createFileGenerator()
}
