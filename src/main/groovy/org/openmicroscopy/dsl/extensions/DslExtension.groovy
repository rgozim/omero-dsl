package org.openmicroscopy.dsl.extensions

import org.gradle.api.Action
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.Project
import org.gradle.api.file.DirectoryProperty

class DslExtension {

    final Project project

    final NamedDomainObjectContainer<VariantExtension> build

    final DirectoryProperty outputDir

    DslExtension(Project project, NamedDomainObjectContainer<VariantExtension> build) {
        this.project = project
        this.build = build
        this.outputDir = project.objects.directoryProperty()
    }

    void dsl(Action<? extends NamedDomainObjectContainer<VariantExtension>> action) {
        action.execute(this.build)
    }

    void dsl(Action<? extends VariantExtension> action) {
        this.build.create('default', action)
    }

}
