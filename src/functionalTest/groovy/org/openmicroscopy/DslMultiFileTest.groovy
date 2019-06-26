package org.openmicroscopy


import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

class DslMultiFileTest extends AbstractBaseTest {

    File conventionOutputDir

    def setup() {
        conventionOutputDir = new File(projectDir, "build/generated/sources/dsl/psql")
    }

    def "can create multiple files with minimal configuration"() {
        given:
        // Output dir is determined by name of configuration
        File multiFileConventionOutputDir = new File(conventionOutputDir, "example")
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
        build("generateExamplePsql")

        then:
        File[] generatedFiles = multiFileConventionOutputDir.listFiles()
        generatedFiles.length > 0
    }

    def "can create multiple files with full user configuration"() {
        given:
        Path outputDir = Paths.get(projectDir.path, "build/full")
        Path multiFileOutputDir = Paths.get("multi")
        Path expectedFinalDir = outputDir.resolve(multiFileOutputDir)

        buildFile << """
            dsl {   
                database = "psql"
                outputDir = new File("${outputDir}")
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