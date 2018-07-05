import org.gradle.api.file.FileCollection

class DslOperation {
    final String name

    String profile

    FileCollection omeXmlFiles

    File template

    File outFile

    File outputPath

    Closure formatOutput

    void setTemplate(String t) {
        setTemplate(new File(t))
    }

    void setTemplate(File t) {
        this.template = t
    }

    void setOutFile(String file) {
        setOutFile(new File(file))
    }

    void setOutFile(File file) {
        this.outFile = file
    }

    void setOutputPath(String dir) {
        setOutputPath(new File(dir))
    }

    void setOutputPath(File dir) {
        this.outputPath = dir
    }

    void omeXmlFiles(FileCollection files) {
        if (omeXmlFiles) {
            omeXmlFiles = omeXmlFiles + files
        } else {
            omeXmlFiles = files
        }
    }

    DslOperation(String name) {
        this.name = name
        profile = "psql"
    }
}
