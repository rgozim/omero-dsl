package org.openmicroscopy.dsl.extensions.specs

import groovy.transform.CompileStatic
import org.gradle.api.file.FileCollection

@CompileStatic
interface OperationSpec {

    File getTemplate()

    FileCollection getOmeXmlFiles()

}
