import org.gradle.api.Project
import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.openmicroscopy.dsl.extensions.DslExtension
import spock.lang.Specification

class DslExtensionTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()
    Project project

    def setup() {
        project = ProjectBuilder.builder().build()
    }

    def "OutputPath is absolute"() {
        when:
        def dsl = new DslExtension()
        dsl.outputPath "someFolder"

        then:
        dsl.outputPath.isAbsolute()
    }

    def "Templates support multiple dirs"() {
        given:
        def folderA = createFilesInFolder(testProjectDir.newFolder("A"))
        def folderB = createFilesInFolder(testProjectDir.newFolder("B"))

        when:
        def dsl = new DslExtension()
        dsl.templateFiles project.fileTree(dir: folderA, include: '*.file')
        dsl.templateFiles project.fileTree(dir: folderB, include: '*.file')

        then:
        dsl.templateDir.size() == 6
    }

    def "OmeXmlFiles support multiple dirs"() {
        given:
        def folderA = createFilesInFolder(testProjectDir.newFolder("A"))
        def folderB = createFilesInFolder(testProjectDir.newFolder("B"))

        when:
        def dsl = new DslExtension()
        dsl.omeXmlFiles project.fileTree(dir: folderA, include: '*.file')
        dsl.omeXmlFiles project.fileTree(dir: folderB, include: '*.file')

        then:
        dsl.omeXmlFiles.size() == 6
    }

    // Create fake ome.xml files
    def createFilesInFolder(File folder) {
        createFile(folder, "fileA.file")
        createFile(folder, "fileB.file")
        createFile(folder, "fileC.file")
        return folder
    }

    def createFile(File folder, String fileName) {
        def file = new File(folder, fileName)
        if (!file.createNewFile()) {
            throw new IOException("File already exists")
        }
        return file
    }

}
