package org.openmicroscopy.dsl.extensions.specs

import groovy.transform.CompileStatic
import org.gradle.api.NamedDomainObjectContainer
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.provider.Property
import org.openmicroscopy.dsl.extensions.MultiFileGeneratorExtension
import org.openmicroscopy.dsl.extensions.SingleFileGeneratorExtension

@CompileStatic
interface DslSpec {

    NamedDomainObjectContainer<MultiFileGeneratorExtension> getMultiFile()

    NamedDomainObjectContainer<SingleFileGeneratorExtension> getSingleFile()

    ConfigurableFileCollection getOmeXmlFiles()

    ConfigurableFileCollection getDatabaseTypes()

    ConfigurableFileCollection getTemplates()

    DirectoryProperty getOutputDir()

    Property<String> getDatabase()

}