package org.openmicroscopy.dsl.extensions

import org.gradle.api.Named
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project

class DslBase implements Named {

    final NamedDomainObjectContainer<MultiFileConfig> multiFile

    final NamedDomainObjectContainer<SingleFileConfig> singleFile

    private final String name

    private final Project project


    @Override
    String getName() {
        return name
    }

    Project getProject() {
        return project
    }
}
