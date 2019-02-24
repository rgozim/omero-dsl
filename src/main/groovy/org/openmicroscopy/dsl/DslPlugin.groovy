package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.tasks.GeneratorBaseTask

import javax.inject.Inject

@CompileStatic
class DslPlugin implements Plugin<Project> {

    private ProjectLayout layout

    @Inject
    DslPlugin(ProjectLayout layout) {
        this.layout = layout
    }

    @Override
    void apply(Project project) {
        project.plugins.apply(DslPluginBase)

        DslExtension dsl = project.extensions.getByType(DslExtension)

        configureForJavaPlugin(project, dsl)
    }

    void applyBasePlugins(Project project) {
        project.plugins.apply(OmeroPlugin)

    }

    void configureForJavaPlugin(Project project, DslExtension dsl) {
        project.plugins.withType(JavaPlugin) { JavaPlugin java ->
            // Configure default outputDir
            JavaPluginConvention javaConvention =
                    project.convention.getPlugin(JavaPluginConvention)

            SourceSet main =
                    javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            // Set source dirs to build names (src/psql/java)
            dsl.build.configureEach { build ->
                main.java.srcDirs layout.projectDirectory.dir(build.name + "/java")
                main.resources.srcDirs layout.projectDirectory.dir(build.name + "/resources")
            }

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
