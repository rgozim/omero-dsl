package org.openmicroscopy.dsl.utils

import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project

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
        return loadFileOrExtract(project, "/" + resFile)

//        if (GradleVersion.current() >= GradleVersion.version('4.8')) {
//            def url = IOUtils.getResource("/" + resFile) // getResource(project, resFile)
//            if (!url) {
//                throw new GradleException("can't find resource file ${resFile}")
//            }
//            def uri = url.toURI()
//            return project.resources.text.fromUri(uri).asFile()
//        } else {
//            return loadFileOrExtract(project, resFile)
//        }
    }

    static private def loadFileOrExtract(Project project, String resourceFile) {
        final def fileLocation = resourceFile
        final def outPutDir = "${project.buildDir}/${resourceFile}"

        // Check if combined file exists
        def result = new File(outPutDir)
        if (!result.exists()) {
            // def classLoader = getClass().getClassLoader()
            // def inputStream = classLoader.getResourceAsStream(fileLocation)
            def inputStream = IOUtils.getResourceAsStream(fileLocation)
            // Copy it to the projects build directory
            FileUtils.copyInputStreamToFile(inputStream, result)
        }
        return result
    }

    static def getResource(Project project, String resFile) {
        def classLoader = ResourceLoader.class.getClassLoader()
        return classLoader.getResource(resFile)
    }

}
