package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.plugins.JavaPlugin
import org.gradle.api.plugins.JavaPluginConvention
import org.gradle.api.provider.ProviderFactory
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.compile.JavaCompile
import org.openmicroscopy.dsl.extensions.OmeroExtension
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.tasks.GeneratorBaseTask

import javax.inject.Inject

import static org.openmicroscopy.dsl.FileTypes.PATTERN_DB_TYPE
import static org.openmicroscopy.dsl.FileTypes.PATTERN_OME_XML
import static org.openmicroscopy.dsl.FileTypes.PATTERN_TEMPLATE

@CompileStatic
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





        // Set some conventions
        dsl.outputDir.convention(project.layout.projectDirectory.dir("src/psql"))
        dsl.omeXmlFiles.setFrom(project.fileTree(dir: "src/main/resources/mappings", include: PATTERN_OME_XML))
        dsl.databaseTypes.setFrom(project.fileTree(dir: "src/main/resources/properties", include: PATTERN_DB_TYPE))
        dsl.templates.setFrom(project.fileTree(dir: "src/main/resources/templates", include: PATTERN_TEMPLATE))

        configureForJavaPlugin(project, omero)
    }

    void configureForJavaPlugin(Project project, OmeroExtension omero) {
        project.plugins.withType(JavaPlugin) { JavaPlugin java ->
            // Configure default outputDir
            JavaPluginConvention javaConvention =
                    project.convention.getPlugin(JavaPluginConvention)

            SourceSet main =
                    javaConvention.sourceSets.getByName(SourceSet.MAIN_SOURCE_SET_NAME)

            if (omero.flavors.isPresent()) {
                // Create a source directory for each flavor
                def flavours = omero.flavors.get()
                flavours.each { String flavor ->
                    main.java.srcDirs layout.projectDirectory.dir(flavor + "/java")
                    main.resources.srcDirs layout.projectDirectory.dir(flavor + "/resources")
                }
            }

            // Configure compileJava task to depend on our tasks
            project.tasks.getByName("compileJava") { JavaCompile jc ->
                jc.dependsOn project.tasks.withType(GeneratorBaseTask)
            }
        }
    }

    // ToDo: fill this functionality in to handle jar naming
    static void configureForMavenPublish(Project project, OmeroExtension omero) {

        omero.flavors.map { List<String> flavors ->
            flavors.each {

            }
        }



        // Set resource dir based on flavour


        // Set some conventions
        dsl.outputDir.convention(project.layout.projectDirectory.dir("src/psql"))
        dsl.omeXmlFiles.setFrom(project.fileTree(dir: "src/main/resources/mappings", include: PATTERN_OME_XML))
        dsl.databaseTypes.setFrom(project.fileTree(dir: "src/main/resources/properties", include: PATTERN_DB_TYPE))
        dsl.templates.setFrom(project.fileTree(dir: "src/main/resources/templates", include: PATTERN_TEMPLATE))
    }

}
