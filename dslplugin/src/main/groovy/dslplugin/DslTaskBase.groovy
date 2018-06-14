package dslplugin

import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.RuntimeConstants
import org.gradle.api.DefaultTask
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.FileCollection
import org.gradle.api.provider.Property
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction

abstract class DslTaskBase extends DefaultTask {

    @InputFiles
    final ConfigurableFileCollection omeXmlFiles = project.layout.configurableFiles()

    @Input
    final Property<String> templateName = project.objects.property(String)

    @Input
    final Property<String> profile = project.objects.property(String)

    @Internal
    final Property<VelocityEngine> velocityEngine = project.objects.property(VelocityEngine)

    void setTemplateName(String name) {
        this.templateName.set(name)
    }

    void setProfile(String profile) {
        this.profile.set(profile)
    }

    void setOmeXmlFiles(FileCollection omeXmlFiles) {
        this.omeXmlFiles.setFrom(omeXmlFiles)
    }

    void setVelocityEngine(VelocityEngine velocityEngine) {
        this.velocityEngine.set(velocityEngine)
    }

    @TaskAction
    def apply() {
        println "FILE_RESOURCE_LOADER_PATH " + (velocityEngine.get().getProperty(RuntimeConstants.FILE_RESOURCE_LOADER_PATH) as String)
        println "RESOURCE_LOADER " + (velocityEngine.get().getProperty(RuntimeConstants.RESOURCE_LOADER) as String)

        def builder = createFileGenerator()
        builder.template = new File(templateName.get())
        builder.omeXmlFiles = omeXmlFiles.getFiles()
        builder.profile = profile.get()
        builder.velocityEngine = velocityEngine.get()
        builder.build().run()
    }

    abstract Generator.Builder createFileGenerator()
}
