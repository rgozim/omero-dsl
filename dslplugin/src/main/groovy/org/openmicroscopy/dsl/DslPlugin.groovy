package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaLibraryPlugin
import org.openmicroscopy.dsl.tasks.DslBaseTask

class DslPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Apply the base plugin
        def plugin = project.plugins.apply(DslPluginBase)

        // Set default for velocity config
        plugin.dslExt.velocity.checkEmptyObjects = false

        // Set compileJava to depend on generateXXX tasks
        setTaskOrdering(project)
    }

    void setTaskOrdering(Project project) {
        project.plugins.withType(JavaLibraryPlugin) {
            project.tasks.named("compileJava").configure { compileJava ->
                compileJava.dependsOn = project.tasks
                        .withType(DslBaseTask)
                        .findAll { it.group == DslPluginBase.GROUP }
            }
        }
    }

}


