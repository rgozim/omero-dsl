package org.openmicroscopy.tasks

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.TaskOutcome
import org.openmicroscopy.AbstractBaseTest

class FileGeneratorTaskTest extends AbstractBaseTest {

    def "can create task without velocity engine configuration"() {
        given:
        File outputFile = new File(projectDir, "genSingleFile.txt")
        buildFile << """
            import org.openmicroscopy.dsl.tasks.FileGeneratorTask

            task genSingleFile(type: FileGeneratorTask) {
                outputFile = file("$outputFile")
                template = file("$templatesDir/single.vm")
                databaseType = file("$databaseTypesDir/psql-types.properties")
                mappingFiles.from(fileTree(dir: "$mappingsDir", include: "**/*.ome.xml"))
            }
        """

        when:
        BuildResult result = build('genSingleFile')

        then:
        result.task(':genSingleFile').outcome == TaskOutcome.SUCCESS
    }

    def "can load mapping files from a directory"() {
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
        BuildResult result = build('genSingleFile')

        then:
        result.task(':genSingleFile').outcome == TaskOutcome.SUCCESS
    }

}

