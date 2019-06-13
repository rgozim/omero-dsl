package org.openmicroscopy

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DslSingleFileTest extends AbstractBaseTest {


    def "can create single file output  with minimal configuration"() {
        given:
        buildFile << """
            dsl {   
                singleFile {
                    example {
                        template = "single.vm"
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

    def "can create single file output with full user configuration"() {
        given:
        buildFile << """
            dsl {   
                database = "psql"
                outputDir = file("some/output/dir")
                omeXmlFiles = fileTree(dir: "${mappingsDir}", include: "**/*.ome.xml")
                databaseTypes = fileTree(dir: "${databaseTypesDir}", include: "**/*.properties")
                templates = fileTree(dir: "${templatesDir}", include: "**/*.vm")
                    
                singleFile {
                    example {
                        template = "single.vm"
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

    def "outputFile overrides dsl.outputDir when absolute"() {
        given:
        Path dslOutputDir = Paths.get(projectDir.path, "build")
        Path absFile = Paths.get(projectDir.path, "some/other/location/example.txt")
        buildFile << """
            dsl {   
                outputDir = new File("${dslOutputDir}")

                singleFile {
                    example {
                        template = "single.vm"
                        outputFile = new File("${absFile}")
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        Files.exists(absFile)
    }

    def "outputFile is relative to dsl.outputDir when not absolute"() {
        given:
        Path dslOutputDir = Paths.get(projectDir.path, "build")
        Path relativeFile = Paths.get("example.txt")
        Path expected = dslOutputDir.resolve(relativeFile)
        buildFile << """
            dsl {   
                outputDir = new File("$dslOutputDir")
            
                singleFile {
                    example {
                        template = "single.vm"
                        outputFile = new File("${relativeFile}")
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        Files.exists(expected)
    }

}
