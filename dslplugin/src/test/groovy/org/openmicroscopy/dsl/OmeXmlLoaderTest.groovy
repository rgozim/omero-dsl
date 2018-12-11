package org.openmicroscopy.dsl

import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openmicroscopy.dsl.utils.OmeXmlLoader
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class OmeXmlLoaderTest extends Specification {

    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "can load ome xml files"() {
        when:
        def files = OmeXmlLoader.loadOmeXmlFiles(project)

        then:
        files.size() > 0
    }

    def "can load ome xml files from plugin resources"() {
        given:
        def buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id "org.openmicroscopy.dsl" version "5.4.8-SNAPSHOT" 
            }
            
            import org.openmicroscopy.dsl.utils.OmeXmlLoader
            
            task printFilesList {
                doLast {
                    OmeXmlLoader.loadOmeXmlFiles(project).each {
                        println it
                    }
                }
            }
        """

        when:
        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments('printFilesList')
                .withDebug(true)
                .build()

        then:
        result.output.contains(".ome.xml")
        result.task(':printFilesList').outcome == SUCCESS
    }

}
