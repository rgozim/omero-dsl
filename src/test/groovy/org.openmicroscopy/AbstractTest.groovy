package org.openmicroscopy

import org.gradle.api.Project
import org.gradle.api.file.ProjectLayout
import org.gradle.api.model.ObjectFactory
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

abstract class AbstractTest extends Specification {

    @Rule
    final TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir

    Project project

    ObjectFactory objects

    ProjectLayout layout

    def setup() {
        projectDir = temporaryFolder.root
        project = ProjectBuilder.builder().withProjectDir(projectDir).build()
        objects = project.objects
        layout = project.layout
    }

}
