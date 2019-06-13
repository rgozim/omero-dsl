package org.openmicroscopy

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DslMultiFileTest extends AbstractBaseTest {

    def "can create multiple files with minimal configuration"() {
        given:
        buildFile << """
            dsl {   
                multiFile {
                    example {
                        template = "multi.vm"
                        formatOutput = { st ->
                            st.getShortname() + ".java"
                        }   
                    }
                }
            }
        """

        when:
        BuildResult result = build("generateExamplePsql")

        then:
        result.task(":generateExamplePsql").outcome == TaskOutcome.SUCCESS
    }

    def "can create multiple files with full user configuration"() {
        given:
        Path outputDir = Paths.get(projectDir.path, "build/full")
        Path multiFileOutputDir = Paths.get("multi")
        Path expectedFinalDir = outputDir.resolve(multiFileOutputDir)

        buildFile << """
            dsl {   
                database = "psql"
                outputDir = file("${outputDir}")
                omeXmlFiles = fileTree(dir: "${mappingsDir}", include: "**/*.ome.xml")
                databaseTypes = fileTree(dir: "${databaseTypesDir}", include: "**/*.properties")
                templates = fileTree(dir: "${templatesDir}", include: "**/*.vm")
                    
                multiFile {
                    example {
                        outputDir = "${multiFileOutputDir}"
                        template = "multi.vm"
                        formatOutput = { st ->
                            st.getShortname() + ".java"
                        } 
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        Files.exists(expectedFinalDir)
        Files.list(expectedFinalDir).count() > 1
    }

    def "outputDir overrides dsl.outputDir when absolute"() {
        given:
        Path dslOutputDir = Paths.get(projectDir.path, "build")
        Path absDir = Paths.get(projectDir.path, "some/other/location")
        buildFile << """
            dsl {   
                outputDir = new File("${dslOutputDir}")

                multiFile {
                    example {
                        outputDir = new File("${absDir}")
                        template = "multi.vm"
                        formatOutput = { st ->
                            st.getShortname() + ".java"
                        } 
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        Files.exists(absDir)
        Files.list(absDir).count() > 1
    }

    def "outputDir is relative to dsl.outputDir when not absolute"() {
        given:
        Path dslOutputDir = Paths.get(projectDir.path, "build")
        Path relativeDir = Paths.get("relativeDir")
        Path expected = dslOutputDir.resolve(relativeDir)
        buildFile << """
            dsl {   
                outputDir = new File("${dslOutputDir}")

                multiFile {
                    example {
                        outputDir = new File("${relativeDir}")
                        template = "multi.vm"
                        formatOutput = { st ->
                            st.getShortname() + ".java"
                        } 
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        Files.exists(expected)
        Files.list(expected).count() > 1
    }

}