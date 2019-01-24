package org.openmicroscopy.dsl

import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.openmicroscopy.dsl.tasks.DslBaseTask

class DslPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Apply the base plugin
        def plugin = project.plugins.apply(DslPluginBase)

        // Set default for velocity config
        plugin.dslExt.velocity.checkEmptyObjects = false

        setTaskOrdering(project)
    }

    def setTaskOrdering(Project project) {
        project.plugins.withType(JavaPlugin) {
            def compileJava = project.tasks.getByName("compileJava")
            if (!compileJava) {
                throw new GradleException("Requires Java plugin")
            }

            def tasks = project.tasks.withType(DslBaseTask).findAll {
                it.group == DslPluginBase.GROUP
            }

            // Ensure generate tasks runs before compileJava
            tasks.each { compileJava.dependsOn(it) }
        }
    }

}
