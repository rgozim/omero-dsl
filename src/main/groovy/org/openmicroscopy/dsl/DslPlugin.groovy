package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.openmicroscopy.dsl.extensions.VariantExtension
import org.openmicroscopy.dsl.tasks.GeneratorBaseTask

@CompileStatic
class DslPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.plugins.apply(DslPluginBase)

        VariantExtension dsl = project.extensions.getByType(VariantExtension)

        configureForJavaPlugin(project, dsl)
    }

    void configureForJavaPlugin(Project project, VariantExtension dsl) {
        project.plugins.withType(JavaPlugin) { JavaPlugin java ->
            // Configure default outputDir
            JavaPluginConvention javaConvention =
                    project.convention.getPlugin(JavaPluginConvention)

            SourceSet main =
                    javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            main.java.srcDirs dsl.outputDir.dir("java")
            main.resources.srcDirs dsl.outputDir.dir("resources")

            // Configure compileJava task to depend on our tasks
            project.tasks.named("compileJava").configure { JavaCompile jc ->
                jc.dependsOn project.tasks.withType(GeneratorBaseTask)
            }
        }
    }

    // ToDo: fill this functionality in to handle jar naming
    static void configureForMavenPublish(Project project) {
        project.plugins.withType(MavenPublishPlugin) { MavenPublishPlugin plugin ->

        }
    }

}
