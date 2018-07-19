package org.openmicroscopy.dsl.utils


import org.gradle.api.Project

class OmeXmlLoader {
    static final def OME_XML_FOLDER = "mappings"
    static final def OME_XML_FILES = [
            "${OME_XML_FOLDER}/acquisition.ome.xml",
            "${OME_XML_FOLDER}/annotations.ome.xml",
            "${OME_XML_FOLDER}/containers.ome.xml",
            "${OME_XML_FOLDER}/display.ome.xml",
            "${OME_XML_FOLDER}/fs.ome.xml",
            "${OME_XML_FOLDER}/jobs.ome.xml",
            "${OME_XML_FOLDER}/meta.ome.xml",
            "${OME_XML_FOLDER}/roi.ome.xml",
            "${OME_XML_FOLDER}/screen.ome.xml"
    ]

    static def loadOmeXmlFiles(Project project) {
        return ResourceLoader.loadFiles(project, OME_XML_FILES)
    }
}
