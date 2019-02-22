package org.openmicroscopy.dsl.utils

import ome.dsl.SemanticType
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.Transformer

class SemanticTypeTransformer implements MultiFileGenerator.FileNameFormatter {

    private final Transformer<? extends String, ? super SemanticType> transformer

    SemanticTypeTransformer(Transformer<? extends String, ? super SemanticType> transformer) {
        this.transformer = transformer
    }

    @Override
    String format(SemanticType semanticType) {
        return transformer.transform(semanticType)
    }

}