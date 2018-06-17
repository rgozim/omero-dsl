

import org.gradle.api.file.FileCollection

class Dsl {
    FileCollection mappingFiles

    File templateDir

    void setTemplateDir(String templateDir) {
        this.templateDir = new File(templateDir)
    }

    void setTemplateDir(File templateDir) {
        this.templateDir = templateDir
    }

    void setMappingFiles(FileCollection files) {
        if (mappingFiles) {
            mappingFiles = mappingFiles + files
        } else {
            mappingFiles = files
        }
    }
}
