package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project

class DatabaseTypeExtension {

    final String name

    final Project project

    File src

    File outputDir

    DatabaseTypeExtension(Project project, String name) {

        this.project = project
        this.name = name

    }
}
