package org.openmicroscopy

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openmicroscopy.dsl.extensions.DslExtension
import org.openmicroscopy.dsl.extensions.MultiFileConfig
import org.openmicroscopy.dsl.extensions.SingleFileConfig
import org.openmicroscopy.dsl.factories.MultiFileGeneratorFactory
import org.openmicroscopy.dsl.factories.SingleFileGeneratorFactory
import spock.lang.Specification

class DslExtensionTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "Templates support multiple dirs"() {
        given:
        def folderA = createFilesInFolder(testProjectDir.newFolder("A"))
        def folderB = createFilesInFolder(testProjectDir.newFolder("B"))

        when:
        DslExtension dsl = createExtension()
        dsl.templates project.fileTree(dir: folderA, include: '*.file')
        dsl.templates project.fileTree(dir: folderB, include: '*.file')

        then:
        dsl.templates.size() == 6
    }

    def "OmeXmlFiles support multiple dirs"() {
        given:
        def folderA = createFilesInFolder(testProjectDir.newFolder("A"))
        def folderB = createFilesInFolder(testProjectDir.newFolder("B"))

        when:
        def dsl = createExtension()
        dsl.omeXmlFiles project.fileTree(dir: folderA, include: '*.file')
        dsl.omeXmlFiles project.fileTree(dir: folderB, include: '*.file')

        then:
        dsl.omeXmlFiles.size() == 6
    }

    // Create fake ome.xml files
    def createFilesInFolder(File folder) {
        createFile(folder, "fileA.file")
        createFile(folder, "fileB.file")
        createFile(folder, "fileC.file")
        return folder
    }

    def createFile(File folder, String fileName) {
        def file = new File(folder, fileName)
        if (!file.createNewFile()) {
            throw new IOException("File already exists")
        }
        return file
    }

    DslExtension createExtension() {
        def multiFileContainer =
                project.container(MultiFileConfig, new MultiFileGeneratorFactory(project))
        def singleFileContainer =
                project.container(SingleFileConfig, new SingleFileGeneratorFactory(project))

        new DslExtension(project, multiFileContainer, singleFileContainer)
    }

}

