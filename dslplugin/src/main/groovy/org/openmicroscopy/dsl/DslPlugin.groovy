package org.openmicroscopy.dsl

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class DslPlugin implements Plugin<Project> {

    static final def OME_XML_FOLDER = "mappings"
    static final def OME_XML_FILES = [
            "acquisition.ome.xml",
            "annotations.ome.xml",
            "containers.ome.xml",
            "display.ome.xml",
            "fs.ome.xml",
            "jobs.ome.xml",
            "meta.ome.xml",
            "roi.ome.xml",
            "screen.ome.xml"
    ]

    @Override
    void apply(Project project) {
        // Apply the base plugin
        def plugin = project.plugins.apply(DslPluginBase)

        // Set default .ome.xml mapping files
        plugin.dslExt.omeXmlFiles = project.files(loadOmeXmlFiles(project))
    }

    def loadOmeXmlFiles(Project project) {
        if (GradleVersion.current() >= GradleVersion.version('4.8')) {
            return OME_XML_FILES.collect {
                def uri = IOUtils.getResource("${OME_XML_FOLDER}/${it}")
                        .toURI()
                project.resources.text.fromUri(uri)
                        .asFile()
            }
        } else {
            return OME_XML_FILES.collect {
                loadFileOrExtract(project, "${OME_XML_FOLDER}/${it}")
            }
        }
    }

    def loadFileOrExtract(Project project, String resourceFile) {
        final def fileLocation = resourceFile
        final def outPutDir = "${project.buildDir}/${resourceFile}"

        // Check if combined file exists
        def result = new File(outPutDir)
        if (!result.exists()) {
            def classLoader = getClass().getClassLoader()
            def inputStream = classLoader.getResourceAsStream(fileLocation)
            // Copy it to the projects build directory
            FileUtils.copyInputStreamToFile(inputStream, result)
        }
        return result
    }
}
