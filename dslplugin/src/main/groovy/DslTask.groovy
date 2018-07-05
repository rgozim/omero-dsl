import ome.dsl.velocity.Generator
import org.apache.velocity.app.VelocityEngine
import org.gradle.api.DefaultTask
import org.gradle.api.file.FileCollection
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Optional
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity
import org.gradle.api.tasks.TaskAction

abstract class DslTask extends DefaultTask {

    @Input
    String profile

    @Input
    @Optional
    Properties velocityProperties

    @InputFiles
    @PathSensitive(PathSensitivity.NONE)
    FileCollection omeXmlFiles

    /**
     * The .vm Velocity template file we want to use to generate
     * our sources
     */
    @InputFile
    @PathSensitive(PathSensitivity.ABSOLUTE)
    File template


    void setTemplate(File file) {
        this.template = setAbsPath(file)
    }

    void template(File file) {
        setTemplate(file)
    }

    protected File setAbsPath(File file) {
        if (!file.is()) {
            return project.file(file)
        } else {
            return file
        }
    }

    @TaskAction
    void apply() {
        // Determine which type of file generator to use
        // Create and init velocity engine
        VelocityEngine ve = new VelocityEngine()
        ve.init(velocityProperties)

        def builder = createGenerator()
        builder.velocityEngine = ve
        builder.omeXmlFiles = omeXmlFiles as Collection
        builder.template = template
        builder.profile = profile
        builder.build().run()
    }

    abstract protected Generator.Builder createGenerator()
}
