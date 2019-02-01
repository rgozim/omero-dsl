package org.openmicroscopy.dsl.factories

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.dsl.extensions.SingleFileGeneratorExtension

@CompileStatic
class SingleFileGeneratorFactory implements NamedDomainObjectFactory<SingleFileGeneratorExtension> {
    final Project project;

    SingleFileGeneratorFactory(Project project) {
        this.project = project
    }

    @Override
    SingleFileGeneratorExtension create(String name) {
        return new SingleFileGeneratorExtension(name, project)
    }
}

