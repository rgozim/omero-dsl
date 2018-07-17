package org.openmicroscopy.dsl.extensions

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
        template = new File(t)
    }

    void template(String t) {
        setTemplate(t)
    }

    void template(File template) {
        this.template = template
    }

    void omeXmlFiles(FileCollection files) {
        if (omeXmlFiles) {
            omeXmlFiles = omeXmlFiles + files
        } else {
            omeXmlFiles = files
        }
    }
}



