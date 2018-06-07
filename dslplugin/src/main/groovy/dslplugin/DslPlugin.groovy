package dslplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

class DslPlugin implements Plugin<Project> {

    /**
     * Sets the group name for the DSLPlugin tasks to reside in.
     * i.e. In a terminal, call `./gradlew tasks` to list tasks in their groups in a terminal
     */
    final def GROUP = 'omero'

    Properties velocityProps

    @Override
    void apply(Project project) {
        // Apply configuration base plugin
        project.plugins.apply(DslPluginBase)

        // Now work on convention
        configureVelocity(project)
        configJavaTasks(project)
    }

    void configureVelocity(final Project project) {
        project.afterEvaluate {
            velocityProps = DslPluginBase.createVelocityProperties(project.dsl.velocity)
        }
    }

    void configJavaTasks(final Project project) {
        project.dsl.generate.all { DslOperation info ->
            def taskName = "process${info.name.capitalize()}"

            // Create task and assign group name
            def task = project.task(taskName, type: DslTask) {
                group = GROUP
                description = 'parses ome.xml files and compiles velocity template'
            }

            // Assign property values to task inputs
            project.afterEvaluate {
                task.velocityProps = velocityProps
                task.template = new File(info.template)
                task.omeXmlFiles = info.omeXmlFiles
                task.outputPath = info.outputPath
                task.outFile = info.outFile
                task.formatOutput = info.formatOutput
            }

            if (project.plugins.hasPlugin(JavaPlugin)) {
                // Ensure the dsltask runs before compileJava
                project.tasks.getByName("compileJava").dependsOn(taskName)
            }
        }
    }


}

