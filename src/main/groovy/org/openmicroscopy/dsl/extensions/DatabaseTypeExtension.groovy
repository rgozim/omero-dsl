package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

@CompileStatic
class DatabaseTypeExtension {

    final String name

    final Project project

    File src

    DirectoryProperty outputDir

    DatabaseTypeExtension(String name, Project project) {
        this.name = name
        this.project = project
        this.outputDir.convention(project.layout.projectDirectory.dir("src/" + name))
    }
}
