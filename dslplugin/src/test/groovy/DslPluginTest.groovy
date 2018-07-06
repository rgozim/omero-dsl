import org.gradle.testkit.runner.GradleRunner
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DslPluginTest extends Specification {

    @Rule
    public final TemporaryFolder testProjectDir = new TemporaryFolder()

    File buildFile

    def setup() {
        buildFile = testProjectDir.newFile('build.gradle')
    }

    def "Dsl tasks generates single file"() {
        given:
        buildFile << """
            task processIce (type: tasks.DslSingleFileTask) {
                template = "cfg.vm"
                outFile = project.file('src/generated/resources/hibernate.cfg.xml')
                omeXmlFiles = project.fileTree(dir: "src/main/resources/mappings", include: '**/*.ome.xml')
            }
        """

        when:
        def result = GradleRunner.create()
                .withProjectDir(testProjectDir.root)
                .build()

        then:
        result.output.contains('Hello world!')
        result.task(":helloWorld").outcome == SUCCESS
    }


}




