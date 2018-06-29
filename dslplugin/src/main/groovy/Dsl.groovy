import org.gradle.api.Project
import org.gradle.api.file.FileCollection

class Dsl {
    FileCollection mappingFiles

    File templateDir

    private Project project

    void setTemplateDir(String templateDir) {
        setTemplateDir(new File(templateDir))
    }

    void setTemplateDir(File templateDir) {
        this.templateDir = project.file(templateDir)
    }

    void setMappingFiles(FileCollection files) {
        if (mappingFiles) {
            mappingFiles = mappingFiles + files
        } else {
            mappingFiles = files
        }
    }

    Dsl(Project project) {
        this.project = project
    }
}
