package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project

class DslPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Apply the base plugin
        def plugin = project.plugins.apply(DslPluginBase)

        // Set default for veolocity config
        plugin.dslExt.velocity.checkEmptyObjects = false

        // Order tasks
        setTaskOrdering(project)
    }

    def setTaskOrdering(Project project) {
        project.afterEvaluate {
            def compileJava = project.tasks.getByName("compileJava")
            if (!compileJava) {
                return
            }

            def generateTaskNames = project.tasks.getNames().findAll() {
                it.startsWith("generate")
            }

            def tasks = generateTaskNames
                    .collect { project.tasks.getByName(it) }
                    .findAll { it.group.equals DslPluginBase.GROUP }

            // Ensure generate tasks runs before compileJava
            tasks.each { compileJava.dependsOn(it) }
        }
    }

}
