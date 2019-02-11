package org.openmicroscopy.dsl

import groovy.transform.CompileStatic

@CompileStatic
class FileTypes {

    public static final String EXTENSION_OME_XML = "ome.xml"

    public static final String EXTENSION_DB_TYPE = "properties"

    public static final String EXTENSION_TEMPLATE = "vm"

    public static final String PATTERN_OME_XML = "**/*.$EXTENSION_OME_XML"

    public static final String PATTERN_DB_TYPE = "**/*-type*.$EXTENSION_DB_TYPE"

    public static final String PATTERN_TEMPLATE = "**/*.$EXTENSION_TEMPLATE"

}
