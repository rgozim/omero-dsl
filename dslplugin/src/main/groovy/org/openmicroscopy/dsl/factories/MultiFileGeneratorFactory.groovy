package org.openmicroscopy.dsl.factories

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.dsl.extensions.MultiFileGeneratorExtension

@CompileStatic
class MultiFileGeneratorFactory implements NamedDomainObjectFactory<MultiFileGeneratorExtension> {
    final Project project

    MultiFileGeneratorFactory(Project project) {
        this.project = project
    }

    @Override
    MultiFileGeneratorExtension create(String name) {
        return new MultiFileGeneratorExtension(name, project)
    }
}

