package org.openmicroscopy.dsl.meta

import org.openmicroscopy.dsl.extensions.OmeroExtension


// Add helper method to OmeroExtension that can configure the
// NamedDomainObjectContainer<VariantExtension>
OmeroExtension.metaClass.build = { Closure closure ->
    try {
        delegate.configure(closure)
    } catch (MissingPropertyException ignored) {
        delegate.create("default", closure)
    }
}
