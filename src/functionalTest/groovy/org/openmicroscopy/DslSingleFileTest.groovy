package org.openmicroscopy

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths


class DslSingleFileTest extends AbstractBaseTest {

    File conventionOutputDir

    def setup() {
        conventionOutputDir = new File(projectDir, "build/generated/sources/dsl/psql")
    }

    def "can create single file output  with minimal configuration"() {
        given:
        String outputFile = "example.txt"
        buildFile << """
            dsl {   
                singleFile {
                    example {
                        template = "single.vm"
                        outputFile = "$outputFile"
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        File result = new File(conventionOutputDir, outputFile)
        result.exists()
    }

    def "can create single file output with full user configuration"() {
        given:
        String customOutputDir = "some/output/dir"
        String outputFile = "example.txt"
        buildFile << """
            dsl {   
                database = "psql"
                outputDir = file("$customOutputDir")
                omeXmlFiles = fileTree(dir: "${mappingsDir}", include: "**/*.ome.xml")
                databaseTypes = fileTree(dir: "${databaseTypesDir}", include: "**/*.properties")
                templates = fileTree(dir: "${templatesDir}", include: "**/*.vm")
                    
                singleFile {
                    example {
                        template = "single.vm"
                        outputFile = "$outputFile"
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        Path result = projectDir.toPath().resolve(Paths.get(customOutputDir, outputFile))
        Files.exists(result)
    }

    def "can generate single file  with inputs configured as directories"() {
        given:
        String outputDir = "build"
        String outputFile = "example.txt"
        buildFile << """
            dsl {   
                database = "psql"
                outputDir = file("$outputDir")
                omeXmlFiles = files("${mappingsDir}")
                databaseTypes = files("${databaseTypesDir}")
                templates = files("${templatesDir}")
                    
                singleFile {
                    example {
                        template = "single.vm"
                        outputFile = "$outputFile"
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        File file = new File(projectDir, "$outputDir/$outputFile")
        file.exists()
    }

    def "outputFile overrides dsl.outputDir when absolute"() {
        given:
        File absFile = new File(projectDir, "some/other/location/example.txt")
        buildFile << """
            dsl {   
                singleFile {
                    example {
                        template = "single.vm"
                        outputFile = new File("$absFile")
                    }
                }
            }
        """

        when:
        build("generateExamplePsql")

        then:
        absFile.exists()
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
