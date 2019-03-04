package org.openmicroscopy.dsl


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.OmeroExtension
import org.openmicroscopy.dsl.tasks.GeneratorBaseTask

import javax.inject.Inject

class DslPlugin implements Plugin<Project> {

    private ProjectLayout layout

    private ProviderFactory provider

    @Inject
    DslPlugin(ProjectLayout layout, ProviderFactory provider) {
        this.layout = layout
        this.provider = provider
    }

    @Override
    void apply(Project project) {
        project.plugins.apply(DslPluginBase)

        OmeroExtension omero = project.extensions.getByType(OmeroExtension)
        DslExtension dsl = (omero as ExtensionAware).extensions.getByType(DslExtension)
        configureForJavaPlugin(project, dsl)
    }

    void configureForJavaPlugin(Project project, DslExtension dsl) {
        project.plugins.withType(JavaPlugin) { JavaPlugin java ->
            // Configure compileJava task to depend on our tasks
            project.tasks.getByName("compileJava") { JavaCompile jc ->
                jc.dependsOn project.tasks.withType(GeneratorBaseTask)
            }

            // Configure default outputDir
            JavaPluginConvention javaConvention =
                    project.convention.getPlugin(JavaPluginConvention)

            SourceSet main =
                    javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            project.afterEvaluate {
                // Create a source directory for each database type
                dsl.databases.get().each { String database ->
                    main.java.srcDir dsl.outputDir.dir("$database/java")
                    main.resources.srcDir dsl.outputDir.dir("$database/resources")
                }
            }

        }
    }

}
