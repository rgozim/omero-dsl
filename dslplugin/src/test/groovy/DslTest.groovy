import org.gradle.testfixtures.ProjectBuilder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class DslTest extends Specification {

    @Rule
    final TemporaryFolder testProjectDir = new TemporaryFolder()

    def setup() {

    }

    def "Template resolves to absolute paths"() {
        given:
        def project = ProjectBuilder.builder().build()

        when:
        def dsl = new Dsl(project)
        dsl.templateDir = testProjectDir.getRoot().name

        then:
        dsl.templateDir.isAbsolute()
    }

    def "OmeXmlFiles support multiple dirs"() {
        given:
        def project = ProjectBuilder.builder().build()
        def folderA = createFolderWithFiles("A")
        def folderB = createFolderWithFiles("B")

        when:
        def dsl = new Dsl(project)
        dsl.mappingFiles project.fileTree(dir: folderA, include: '*.ome.xml')
        dsl.mappingFiles project.fileTree(dir: folderB, include: "*.ome.xml")

        then:
        dsl.mappingFiles.size() == 6
    }

    // Create fake ome.xml files
    def createFolderWithFiles(String folderName) {
        File folder = testProjectDir.newFolder(folderName)
        createFile(folder, "fileA.ome.xml")
        createFile(folder, "fileB.ome.xml")
        createFile(folder, "fileC.ome.xml")
        return folder
    }

    def createFile(File folder, String fileName) {
        def file = new File(folder.path + File.separator + fileName)
        if (!file.createNewFile()) {
            throw new IOException("File already exists")
        }
        return file
    }

}
