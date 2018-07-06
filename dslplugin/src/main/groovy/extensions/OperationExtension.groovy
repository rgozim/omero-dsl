package extensions

import org.gradle.api.file.FileCollection

class OperationExtension {

    public final String name

    String profile

    File template

    FileCollection omeXmlFiles

    protected OperationExtension(String name) {
        this.name = name
        this.profile = "psql"
    }

    void setTemplate(String t) {
        setTemplate(new File(t))
    }

    void template(String t) {
        setTemplate(t)
    }

    void setTemplate(File t) {
        this.template = t
    }

    void template(File t) {
        setTemplate(t)
    }

    void omeXmlFiles(FileCollection files) {
        if (omeXmlFiles) {
            omeXmlFiles = omeXmlFiles + files
        } else {
            omeXmlFiles = files
        }
    }
}



