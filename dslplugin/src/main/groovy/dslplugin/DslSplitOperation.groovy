package dslplugin

import ome.dsl.SemanticType
import org.gradle.api.Transformer
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property

class DslSplitOperation {

    final Property<String> profile
    final RegularFileProperty template
    final DirectoryProperty outputPath
    final Property<Transformer<String, SemanticType>> filenameFormatter

    void setProfile(String profile) {
        this.profile.set(profile)
    }

    void setTemplate(File template) {
        this.template.set(template)
    }

    void setTemplate(String template) {
        this.template.set(new File(template))
    }


}
