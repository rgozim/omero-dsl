package org.openmicroscopy.dsl

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.openmicroscopy.dsl.extensions.OmeroExtension

class OmeroPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        OmeroExtension omero =
                project.extensions.create("omero", OmeroExtension)

        // Create a configuration for every flavor
        project.plugins.withType(JavaBasePlugin) {
            omero.flavors.get().each { String flavor ->
                final String flavorImplementation = flavor + "Implementation"
                ConfigurationContainer configurations = project.getConfigurations()
                Configuration compileConfiguration = configurations.findByName(flavorImplementation)
                if (compileConfiguration == null) {
                    configurations.create(flavorImplementation)
                            .setVisible(false)
                            .extendsFrom(configurations.getByName("implementation"))
                            .setDescription("Implementation configuration for flavor $flavor")
                }

                project.plugins.withType(JavaLibraryPlugin) { JavaLibraryPlugin plugin ->
                    final String flavorApi = flavor + "Api"
                    compileConfiguration = configurations.findByName(flavorImplementation)
                    if (compileConfiguration == null) {
                        configurations.create(flavorApi)
                                .setVisible(false)
                                .extendsFrom(configurations.getByName("api"))
                                .setDescription("API configuration for flavor $flavor")
                    }
                }

            }
        }
    }
}

