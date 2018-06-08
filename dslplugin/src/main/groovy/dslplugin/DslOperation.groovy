package dslplugin

import org.gradle.api.file.FileCollection

class DslOperation {
    final String name

    String template

    FileCollection omeXmlFiles

    File outputPath

    File outFile

    Closure formatOutput

    DslOperation(String name) {
        this.name = name
    }
}
