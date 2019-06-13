package org.openmicroscopy

import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

class AbstractBaseTest extends AbstractGroovyTest {

    File databaseTypesDir
    File mappingsDir
    File templatesDir

    def setup() {
        databaseTypesDir = new File(projectDir, "src/main/resources/properties")
        mappingsDir = new File(projectDir, "src/main/resources/mappings")
        templatesDir = new File(projectDir, "src/main/resources/templates")

        writeSettingsFile()
        copyDatabaseTypes(databaseTypesDir)
        copyOmeXmls(mappingsDir)
        copyTemplates(templatesDir)
    }

    private void writeSettingsFile() {
        settingsFile << groovySettingsFile()
    }

    private void copyDatabaseTypes(File outputDir) {
        Path psql = getResource("/psql-types.properties")

        copyFile(psql, outputDir.toPath())
    }

    private void copyOmeXmls(File outputDir) {
        Path type = getResource("/type.ome.xml")

        copyFile(type, outputDir.toPath())
    }

    private void copyTemplates(File outputDir) {
        Path single = getResource("/single.vm")
        Path multi = getResource("/multi.vm")

        copyFile(single, outputDir.toPath())
        copyFile(multi, outputDir.toPath())
    }

    private void copyFile(Path fileToCopy, Path targetDir) {
        if (!Files.exists(targetDir)) {
            Files.createDirectories(targetDir)
        }

        Path targetFile = targetDir.resolve(fileToCopy.getFileName())
        Files.copy(fileToCopy, targetFile, StandardCopyOption.REPLACE_EXISTING)
    }

    private Path getResource(String name) {
        Paths.get(Paths.getResource(name).toURI())
    }

}
