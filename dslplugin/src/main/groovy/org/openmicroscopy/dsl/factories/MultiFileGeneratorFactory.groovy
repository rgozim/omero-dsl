package org.openmicroscopy.dsl.factories

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.dsl.extensions.MultiFileConfig

@CompileStatic
class MultiFileGeneratorFactory implements NamedDomainObjectFactory<MultiFileConfig> {
    final Project project

    MultiFileGeneratorFactory(Project project) {
        this.project = project
    }

    @Override
    MultiFileConfig create(String name) {
        return new MultiFileConfig(name, project)
    }
}

