package org.openmicroscopy.dsl


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.openmicroscopy.dsl.utils.OmeXmlLoader

class DslPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        // Apply the base plugin
        def plugin = project.plugins.apply(DslPluginBase)

        // Set default .ome.xml mapping files
        plugin.dslExt.omeXmlFiles = OmeXmlLoader.loadOmeXmlFiles(project)
    }
}
