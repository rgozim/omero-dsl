package dslplugin

import ome.dsl.SemanticType
import org.gradle.api.Transformer
import org.gradle.api.file.FileCollection

class DslOperation {
    final String name

    String profile

    FileCollection omeXmlFiles

    File template

    File outFile

    File outputPath

    Transformer<String, SemanticType> formatOutput

    void template(String template) {
        this.template = new File(template)
    }

    void setTemplate(String template) {
        this.template = new File(template)
    }

    DslOperation(String name) {
        this.name = name
        profile = "psql"
    }
}
