package org.openmicroscopy.dsl.utils

import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator

class SemanticTypeClosure implements MultiFileGenerator.FileNameFormatter {

    private final Closure closure

    SemanticTypeClosure(Closure closure) {
        this.closure = closure
    }

    @Override
    String format(SemanticType semanticType) {
        Object val = closure.call(semanticType)
        return val == null ? null : val.toString()
    }

}
