package dslplugin

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin

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
        project.afterEvaluate {
            // Set some defaults for velocity
            VelocityExtension ve = project.dsl.velocity
            ve.loggerClassName = project.getLogger().getClass().getName()
            if (project.plugins.hasPlugin(JavaPlugin)) {
                ve.fileResourceLoaderPath = "${project.sourceSets.main.output.resourcesDir}"
            } else {
                ve.fileResourceLoaderPath = "${project.projectDir}/src/main/resources"
            }

            // Assign default velocity config to each dsl task
            basePlugin.dslTasks.each { task ->
                task.velocityProperties = ve.properties.get()

                if (project.plugins.hasPlugin(JavaPlugin)) {
                    // Ensure the DslTask runs before compileJava
                    project.tasks.getByPath("compileJava").dependsOn(task)
                }
            }
        }
    }
}

