package org.openmicroscopy.dsl.factories

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.MultiFileConfig
import org.openmicroscopy.dsl.extensions.SingleFileConfig

class DslFactory implements NamedDomainObjectFactory<DslExtension> {

    private final Project project

    DslFactory(Project project) {
        this.project = project
    }

    @Override
    DslExtension create(String name) {
        def multiFileContainer =
                project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))

        return new DslExtension(name, project, singleFileContainer, multiFileContainer)
    }

}
