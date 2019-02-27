package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.publish.maven.plugins.MavenPublishPlugin
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.openmicroscopy.dsl.extensions.OmeroExtension
import org.openmicroscopy.dsl.extensions.VariantExtension
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

        OmeroExtension omero = project.extensions.getByType(OmeroExtension)

        NamedDomainObjectContainer<VariantExtension> build =
                omero.metaClass["build"] as NamedDomainObjectContainer<VariantExtension>

        configureForJavaPlugin(project, build)
    }

    void configureForJavaPlugin(Project project, NamedDomainObjectContainer<VariantExtension> build) {
        project.plugins.withType(JavaPlugin) { JavaPlugin java ->
            // Configure default outputDir
            JavaPluginConvention javaConvention =
                    project.convention.getPlugin(JavaPluginConvention)

            SourceSet main =
                    javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            // Set source dirs to build names (src/psql/java)
//            build.all { VariantExtension variant ->
//                main.java.srcDirs layout.projectDirectory.dir(variant.name + "/java")
//                main.resources.srcDirs layout.projectDirectory.dir(variant.name + "/resources")
//            }

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
