package org.openmicroscopy.dsl.extensions

import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.ListProperty

import javax.inject.Inject

class OmeroExtension {

    private final ListProperty<String> flavors

    @Inject
    OmeroExtension(ObjectFactory objectFactory, NamedDomainObjectContainer<DslExtension> build) {
        this.flavors = objectFactory.listProperty(String)
        this.build = build
    }

    ListProperty<String> getFlavors() {
        return flavors
    }

    void flavors(String... flavors) {
        this.flavors.addAll(flavors)
    }

    void setFlavors(Iterable<String> flavors) {
        this.flavors.set(flavors)
    }

    // Add DSL

    // Add API

}
