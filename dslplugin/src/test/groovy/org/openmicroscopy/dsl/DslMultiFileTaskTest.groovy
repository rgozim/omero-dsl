package org.openmicroscopy.dsl

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification
import spock.lang.Stepwise

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

@Stepwise
class DslMultiFileTaskTest extends Specification {

    @Rule
    TemporaryFolder testProjectDir = new TemporaryFolder()
    File settingsFile
    File buildFile
    File resourcesDir

    def setup() {
        resourcesDir = new File(System.properties['resourcesDir'] as String)
        settingsFile = testProjectDir.newFile('settings.gradle')
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id "org.openmicroscopy.dsl"
            }
        """
    }

    def "can output a single generated file"() {
        given:
        buildFile << """
            import org.openmicroscopy.dsl.tasks.DslMultiFileTask
            import org.openmicroscopy.dsl.utils.OmeXmlLoader
             
            task generateJava(type: DslMultiFileTask) {
                profile "psql"
                template "$resourcesDir/object.vm"
                outputPath project.buildDir
                formatOutput { st ->
                    "${st.getPackage()}/${st.getShortname()}.java"
                }
                omeXmlFiles OmeXmlLoader.loadOmeXmlFiles(project)
            }
        """

        when:
        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateJava', "-PresourcesDir=" + resourcesDir)
                .build()

        then:
        result.task(":generateJava").outcome == SUCCESS
    }

}