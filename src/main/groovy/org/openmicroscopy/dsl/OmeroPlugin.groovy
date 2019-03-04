package org.openmicroscopy.dsl


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.Configuration
import org.gradle.api.artifacts.ConfigurationContainer
import org.gradle.api.plugins.JavaBasePlugin
import org.gradle.api.plugins.JavaLibraryPlugin
import org.openmicroscopy.dsl.extensions.OmeroExtension

class OmeroPlugin implements Plugin<Project> {

    public static final String EXTENSION_OMERO = "omero"

    @Override
    void apply(Project project) {
        OmeroExtension omero =
                project.extensions.create(EXTENSION_OMERO, OmeroExtension)

        // Create a configuration for every flavor
        omero.flavors.each { String flavor ->
            ConfigurationContainer configs = project.getConfigurations()

            project.plugins.withType(JavaBasePlugin) {
                String flavorImplementation = flavor + "Implementation"
                findOrCreateConfig(configs, flavorImplementation)
                        .setVisible(false)
                        .extendsFrom(configs.findByName("implementation"))
                        .setDescription("Configuration for flavor $flavor")
            }

            project.plugins.withType(JavaLibraryPlugin) {
                String flavorApi = flavor + "Api"
                findOrCreateConfig(configs, flavorApi)
                        .setVisible(false)
                        .extendsFrom(configs.findByName("api"))
                        .setDescription("Configuration for flavor $flavor")
            }
        }

//        omero.flavors.each { String flavor ->
//            // Create source sets for flavors
//            project.plugins.withType(JavaBasePlugin) {
//                // Configure default outputDir
//                JavaPluginConvention javaConvention =
//                        project.convention.getPlugin(JavaPluginConvention)
//
//                javaConvention.sourceSets.create(flavor, new Action<SourceSet>() {
//                    @Override
//                    void execute(SourceSet t) {
//                        t.java {
//                            srcDirs = ["src/$flavor/java"]
//                        }
//                        t.resources {
//                            srcDirs = ["src/$flavor/resources"]
//                        }
//                    }
//                })
//            }
//        }
    }

    Configuration findOrCreateConfig(ConfigurationContainer configs, String configName) {
        Configuration config = configs.findByName(configName)
        if (config == null) {
            configs.create(configName)
        }
        return config
    }

}

