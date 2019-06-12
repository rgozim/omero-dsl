package org.openmicroscopy

import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.openmicroscopy.dsl.DslPluginBase

class DslPluginBaseTest extends AbstractTest {

    def "getOutputDirProvider combines base and child dirs"() {
        given:
        DirectoryProperty baseDir = layout.buildDirectory

        Property<File> childDir = objects.property(File)
                .value(new File("child"))

        when:
        DslPluginBase plugin = createDslPluginBase()
        Provider<Directory> result = plugin.getOutputDirProvider(baseDir, childDir)

        then:
        result.get().asFile.path == baseDir.asFile.get().path + '/child'
    }

    def "getOutputDirProvider uses child dir if absolute"() {
        given:
        File expectedDir = new File("/some/madeup/full/path/child")

        Property<File> childDir = objects.property(File)
                .value(expectedDir)

        DirectoryProperty baseDir = layout.buildDirectory

        when:
        DslPluginBase plugin = createDslPluginBase()
        Provider<Directory> result = plugin.getOutputDirProvider(baseDir, childDir)

        then:
        result.get().asFile.path == expectedDir.path
    }

    def "getOutputFileProvider combines base and child dirs"() {
        given:
        DirectoryProperty baseDir = layout.buildDirectory

        Property<File> childDir = objects.property(File)
                .value(new File("child.txt"))

        File expectPath = new File(baseDir.get().asFile, "child.txt")

        when:
        DslPluginBase plugin = createDslPluginBase()
        Provider<RegularFile> result = plugin.getOutputFileProvider(baseDir, childDir)

        then:
        result.get().asFile.path == expectPath.path
    }

    def "getOutputFileProvider uses child dir if absolute"() {
        given:
        File expectedFile = new File("/some/madeup/full/path/file.txt")

        Property<File> childFile = objects.property(File)
                .value(expectedFile)

        DirectoryProperty baseDir = layout.buildDirectory

        when:
        DslPluginBase plugin = createDslPluginBase()
        Provider<RegularFile> result = plugin.getOutputFileProvider(baseDir, childFile)

        then:
        result.get().asFile.path == expectedFile.path
    }

    DslPluginBase createDslPluginBase() {
        new DslPluginBase(project.objects, project.providers)
    }
}
