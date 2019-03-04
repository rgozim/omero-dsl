package org.openmicroscopy.dsl.extensions

import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

class OmeroExtension {

    private List<String> flavors

    @Inject
    OmeroExtension(ObjectFactory objectFactory) {
        this.flavors = []
    }

    List<String> getFlavors() {
        return flavors
    }

    void flavors(String... flavors) {
        this.flavors.addAll(flavors)
    }

    void setFlavors(Iterable<String> flavors) {
        this.flavors = flavors.asList()
    }

    // Add DSL

    // Add API

}
