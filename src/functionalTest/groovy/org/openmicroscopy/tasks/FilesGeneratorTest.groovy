package org.openmicroscopy.tasks


import org.gradle.testkit.runner.UnexpectedBuildFailure
import org.openmicroscopy.AbstractBaseTest

class FilesGeneratorTest extends AbstractBaseTest {

    def "can generate multiple files"() {
        given:
        File outputDir = new File(projectDir, "build")
        buildFile << """
            import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

            task genFiles(type: FilesGeneratorTask) {
                outputDir = file("$outputDir")
                template = file("$templatesDir/single.vm")
                databaseType = file("$databaseTypesDir/psql-types.properties")
                mappingFiles.from(files("$mappingsDir"))
                formatOutput = { st ->
                    st.getShortname() + ".java"
                }
            }
        """

        when:
        build('genFiles')

        then:
        outputDir.listFiles().length > 0
    }

    def "fails if formatOutput is unassigned"() {
        given:
        File outputDir = new File(projectDir, "build")
        buildFile << """
            import org.openmicroscopy.dsl.tasks.FilesGeneratorTask

            task genFiles(type: FilesGeneratorTask) {
                outputDir = file("$outputDir")
                template = file("$templatesDir/single.vm")
                databaseType = file("$databaseTypesDir/psql-types.properties")
                mappingFiles.from(files("$mappingsDir"))
            }
        """

        when:
        build('genFiles')

        then:
        thrown(UnexpectedBuildFailure)
    }


}
