package org.openmicroscopy.tasks


import org.openmicroscopy.AbstractBaseTest

class FileGeneratorTest extends AbstractBaseTest {

    def "can generate as single file"() {
        given:
        File outputFile = new File(projectDir, "genSingleFile.txt")
        buildFile << """
            import org.openmicroscopy.dsl.tasks.FileGeneratorTask

            task genSingleFile(type: FileGeneratorTask) {
                outputFile = file("$outputFile")
                template = file("$templatesDir/single.vm")
                databaseType = file("$databaseTypesDir/psql-types.properties")
                mappingFiles.from(files("$mappingsDir"))
            }
        """

        when:
        build('genSingleFile')

        then:
        outputFile.exists()
    }

}

