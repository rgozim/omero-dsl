package dslplugin

import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty

class Dsl {
    final ConfigurableFileCollection mappingFiles

    final DirectoryProperty templateDir

    Dsl(Project project) {
        mappingFiles = project.layout.configurableFiles()
        templateDir = project.layout.directoryProperty()
    }
}
