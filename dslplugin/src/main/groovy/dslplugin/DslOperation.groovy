package dslplugin

import org.gradle.api.file.FileCollection

class DslOperation {
    final String name

    String profile

    FileCollection omeXmlFiles

    File template

    File outFile

    File outputPath

    Closure formatOutput

    void template(String t) {
        this.template = new File(t)
    }

    void setTemplate(String t) {
        template(t)
    }

    void outFile(String file) {
        this.outFile = new File(file)
    }

    void setOutFile(String file) {
        outFile(file)
    }

    void outputPath(String dir) {
        this.outputPath = new File(dir)
    }

    void setOutputPath(String dir) {
        outputPath(dir)
    }


    DslOperation(String name) {
        this.name = name
        profile = "psql"
    }
}
