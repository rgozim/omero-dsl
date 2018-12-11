package org.openmicroscopy.dsl

import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

import static org.gradle.testkit.runner.TaskOutcome.SUCCESS

class DslSingleFileTaskTest extends Specification {

    @Rule TemporaryFolder testProjectDir = new TemporaryFolder()
    File settingsFile
    File buildFile
    File resourcesDir

    def setup() {
        resourcesDir = new File(System.properties['resourcesDir'] as String)
        settingsFile = testProjectDir.newFile('settings.gradle')
        buildFile = testProjectDir.newFile('build.gradle')
        buildFile << """
            plugins {
                id "org.openmicroscopy.dsl" version "5.4.8-SNAPSHOT"
            }
        """
    }

    def "can output a single generated file"() {
        given:
        buildFile << """
            import org.openmicroscopy.dsl.tasks.DslSingleFileTask
            import org.openmicroscopy.dsl.utils.OmeXmlLoader
             
            task generateHibernate(type: DslSingleFileTask) {
                profile "psql"
                outFile "hibernate.cfg.xml"
                template "$resourcesDir/cfg.vm"
                omeXmlFiles OmeXmlLoader.loadOmeXmlFiles(project)
            }
        """

        when:
        def result = GradleRunner.create()
                .withPluginClasspath()
                .withProjectDir(testProjectDir.root)
                .withArguments('generateHibernate', "-PresourcesDir=" + resourcesDir)
                .build()

        then:
        result.task(":generateHibernate").outcome == SUCCESS
    }

}