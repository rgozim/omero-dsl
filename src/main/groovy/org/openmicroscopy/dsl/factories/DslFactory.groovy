package org.openmicroscopy.dsl.factories

import org.gradle.api.NamedDomainObjectFactory
import org.gradle.api.Project
import org.openmicroscopy.dsl.extensions.VariantExtension
import org.openmicroscopy.dsl.extensions.MultiFileConfig
import org.openmicroscopy.dsl.extensions.SingleFileConfig

class DslFactory implements NamedDomainObjectFactory<VariantExtension> {

    private final Project project

    DslFactory(Project project) {
        this.project = project
    }

    @Override
    VariantExtension create(String name) {
        def multiFileContainer =
                project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))

        return new VariantExtension(name, project, multiFileContainer, singleFileContainer)
    }

}
