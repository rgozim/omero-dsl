package org.openmicroscopy.dsl.extensions.specs

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.FileCollection
import org.openmicroscopy.dsl.extensions.MultiFileGeneratorExtension
import org.openmicroscopy.dsl.extensions.SingleFileGeneratorExtension

@CompileStatic
interface DslSpec {

    NamedDomainObjectContainer<MultiFileGeneratorExtension> getMultiFile()

    NamedDomainObjectContainer<SingleFileGeneratorExtension> getSingleFile()

    FileCollection getOmeXmlFiles()

    FileCollection getDatabaseTypes()

    FileCollection getTemplates()

    File getOutputDir()

    String getDatabase()

}