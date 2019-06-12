package org.openmicroscopy

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class DslSingleFileTest extends AbstractGoorvyTest {

    def setup() {
        writeSettingsFile()
        copyDatabaseTypes()
        copyOmeXmls()
        copyTemplates()
    }

    def "Passes gradle tasks command"() {
        when:
        BuildResult result = build('tasks')

        then:
        result.task(":tasks").outcome == TaskOutcome.SUCCESS
    }

    def "Check copied files exist"() {
        buildFile << """
            task printFiles() {
                doLast {
                    def resources = fileTree("src/main/resources").matching { include "**/*" }
                    resources.files.each {
                        println it
                    }
                }
            }
        """

        when:
        BuildResult result = build("printFiles")

        then:
        result.output
    }

    def "Passes with minimal configuration on single file generation"() {
        given:
        buildFile << """
            dsl {   
                singleFile {
                    example {
                        template = "simple.vm"
                        outputFile = "example.txt"
                    }
                }
            }
        """

        when:
        BuildResult result = build("generateExamplePsql")

        then:
        result.task(":generateExamplePsql").outcome == TaskOutcome.SUCCESS
    }

    def "SingleFile overrides dsl outputDir when absolute"() {
        given:
        File absFile = new File(projectDir, "example.txt")

        buildFile << """
            dsl {   
                singleFile {
                    example {
                        template = "simple.vm"
                        outputFile = "${absFile}"
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        absFile.exists()
    }

    def "SingleFile is relative to DslExtension outputDir when not absolute"() {


    }

    private void writeSettingsFile() {
        settingsFile << groovySettingsFile()
    }

    private void copyDatabaseTypes() {
        Path targetDir = Paths.get(projectDir.path, "src/main/resources/properties")
        Path psql = Paths.get(Paths.getResource("/psql-types.properties").toURI())
        copyFile(psql, targetDir)
    }

    private void copyOmeXmls() {
        Path targetDir = Paths.get(projectDir.path, "src/main/resources/mappings")
        Path type = Paths.get(Paths.getResource("/type.ome.xml").toURI())
        copyFile(type, targetDir)
    }

    private void copyTemplates() {
        Path targetDir = Paths.get(projectDir.path, "src/main/resources/templates")
        Path type = Paths.get(Paths.getResource("/simple.vm").toURI())
        copyFile(type, targetDir)
    }

    private void copyFile(Path fileToCopy, Path targetDir) {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir)
        }

        Path targetFile = targetDir.resolve(fileToCopy.getFileName())
        Files.copy(fileToCopy, targetFile, StandardCopyOption.REPLACE_EXISTING)
    }

}
