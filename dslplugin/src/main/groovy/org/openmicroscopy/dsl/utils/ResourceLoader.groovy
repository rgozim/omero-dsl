package org.openmicroscopy.dsl.utils

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.util.GradleVersion

class ResourceLoader {

    static def loadFiles(Project project, List<String> fileList) {
        return fileList.collect {
            loadFile(project, it)
        }
    }

    static def loadFiles(Project project, String[] fileList) {
        return fileList.collect {
            loadFile(project, it)
        }
    }

    static def loadFile(Project project, String resFile) {
        if (GradleVersion.current() >= GradleVersion.version('4.8')) {
            def uri = IOUtils.getResource(resFile).toURI()
            return project.resources.text.fromUri(uri).asFile()
        } else {
            return loadFileOrExtract(project, resFile)
        }
    }

    static private def loadFileOrExtract(Project project, String resourceFile) {
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
