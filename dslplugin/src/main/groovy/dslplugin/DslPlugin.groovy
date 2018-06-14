package dslplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.RegularFile
import org.gradle.api.plugins.JavaPlugin

import java.nio.file.Paths

class DslPlugin implements Plugin<Project> {

    DslPluginBase basePlugin

    @Override
    void apply(Project project) {
        // Apply configuration base plugin
        basePlugin = project.plugins.apply(DslPluginBase)

        // Now work on convention
        applyDefaultConfigs(project)
    }

    /**
     * Sets up default values for Velocity configuration
     * @param project
     */
    void applyDefaultConfigs(Project project) {
        Dsl dsl = project.dsl
        VelocityExtension ve = dsl.velocity

        // Set some defaults for velocity
        ve.loggerClassName = project.getLogger().getClass().getName()

        // Assign default velocity config to each dsl task
        basePlugin.dslTasks.each { task ->
            task.template =  dsl.templateDir.file(task)
            task.omeXmlFiles = dsl.mappingFiles
            task.velocityProperties = ve.properties

            if (project.plugins.hasPlugin(JavaPlugin)) {
                // Ensure the DslTask runs before compileJava
                project.tasks.getByPath("compileJava").dependsOn(task)
            }
        }
    }
}

