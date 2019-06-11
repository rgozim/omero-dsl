package org.openmicroscopy


import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openmicroscopy.dsl.DslBase
import spock.lang.Specification

class DslBaseHelpersTest extends Specification {

    @Rule
    TemporaryFolder temporaryFolder = new TemporaryFolder()

    File projectDir

    Project project

    def setup() {
        projectDir = temporaryFolder.root
        project = ProjectBuilder.builder()
                .withProjectDir(projectDir)
                .build()
    }

    def "Can find database types file in collection"() {
        // Create temp files
        File databaseTypesFolder = temporaryFolder.newFolder()
        createDbTypeFilesInFolder(databaseTypesFolder, "properties")

        when:
        File dbType = DslBase.findDatabaseType(project.files(databaseTypesFolder), "a")

        then:
        dbType.isFile()
    }

    def "Can find template file in collection"() {
        // Create temp files
        File databaseTypesFolder = temporaryFolder.newFolder()
        createTemplateFilesInFolder(databaseTypesFolder)

        when:
        File dbType = DslBase.findTemplate(project.files(databaseTypesFolder), )

        then:
        dbType.isFile()
    }

    // Create fake "-types.properties" files
    def createDbTypeFilesInFolder(File folder) {
        createFile(folder, "a-types.properties")
        createFile(folder, "b-types.properties")
        createFile(folder, "c-types.properties")
        return folder
    }

    // Create fake "-types.properties" files
    def createTemplateFilesInFolder(File folder) {
        createFile(folder, "a.vm")
        createFile(folder, "b.vm")
        createFile(folder, "c.vm")
        return folder
    }

    def createFile(File folder, String fileName) {
        def file = new File(folder, fileName)
        if (!file.createNewFile()) {
            throw new IOException("File already exists")
        }
        return file
    }

}
