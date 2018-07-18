package org.openmicroscopy.dsl

import org.apache.commons.io.FileUtils
import org.gradle.api.Plugin
import org.gradle.api.Project

class DslPlugin implements Plugin<Project> {

    static final String OME_XML_FOLDER = "mappings"

    @Override
    void apply(Project project) {
        // Apply the base plugin
        def plugin = project.plugins.apply(DslPluginBase)

        // Add our plugin opinions
        // 1) load local ome.xml files for mapping files (default)
        // plugin.dslExt.mappingFiles = project.files(loadOmeXmlFiles(project))

        project.tasks.create("loadOmeXmlFiles") {
            println getResourceFolderFiles("mappings")
        }
    }


    def loadOmeXmlFiles(Project project) {
        def xmlFiles = getResourceFolderFiles(OME_XML_FOLDER)
        return xmlFiles.collect { loadResFile(project, it) }
    }

    def loadResFile(Project project, File file) {
        println file
        final def projectFile = "${project.buildDir}/${file.path}"
        def result = new File(projectFile)
        if (!result.exists()) {
            def classLoader = getClass().getClassLoader()
            def inputStream = classLoader.getResourceAsStream(file.name)
            // Copy it to the projects build directory
            FileUtils.copyInputStreamToFile(inputStream, result)
        }
        return result
    }

    def getResourceFolderFiles(String folder) {
        ClassLoader loader = getClass().getClassLoader()
        URL url = loader.getResource(folder)
        String path = url.getPath()
        return new File(path).listFiles()
    }
}
