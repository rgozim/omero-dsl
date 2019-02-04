package org.openmicroscopy.dsl.extensions.specs


import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.FileCollection
import org.openmicroscopy.dsl.extensions.MultiFileGeneratorExtension
import org.openmicroscopy.dsl.extensions.SingleFileGeneratorExtension

interface DslSpec {

    NamedDomainObjectContainer<MultiFileGeneratorExtension> getMultiFile()

    NamedDomainObjectContainer<SingleFileGeneratorExtension> getSingleFile()

    FileCollection getOmeXmlFiles()

    FileCollection getDatabaseTypes()

    FileCollection getTemplates()

    File getOutputDir()

    String getDatabase()

}

//@CompileStatic
//interface DslSpec {
//
//    NamedDomainObjectContainer<MultiFileGeneratorExtension> getMultiFile()
//
//    NamedDomainObjectContainer<SingleFileGeneratorExtension> getSingleFile()
//
//    FileCCacheConstants getOmeXmlFiles()
//
//    ConfigurableFileCollection getDatabaseTypes()
//
//    ConfigurableFileCollection getTemplates()
//
//    File getOutputDir()
//
//    String getDatabase()
//
//}