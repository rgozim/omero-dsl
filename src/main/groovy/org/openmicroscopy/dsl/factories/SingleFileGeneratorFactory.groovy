package org.openmicroscopy.dsl.factories

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.dsl.extensions.SingleFileConfig

@CompileStatic
class SingleFileGeneratorFactory implements NamedDomainObjectFactory<SingleFileConfig> {
    final Project project;

    SingleFileGeneratorFactory(Project project) {
        this.project = project
    }

    @Override
    SingleFileConfig create(String name) {
        return new SingleFileConfig(name, project)
    }
}

