package org.openmicroscopy.dsl.utils


import ome.dsl.SemanticType
import org.gradle.api.Transformer

class SemanticTypeTransformer implements Transformer<String, SemanticType> {

    Closure closure

    SemanticTypeTransformer(Closure<SemanticType> closure) {
        this.closure = closure
    }

    @Override
    String transform(SemanticType semanticType) {
        return closure(semanticType)
    }
}
