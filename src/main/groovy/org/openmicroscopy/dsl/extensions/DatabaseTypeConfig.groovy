package org.openmicroscopy.dsl.extensions

import org.gradle.api.Project

class DatabaseTypeConfig {

    final String name

    final Project project

    File src

    File outputDir

    DatabaseTypeConfig(Project project, String name) {

        this.project = project
        this.name = name

    }
}
