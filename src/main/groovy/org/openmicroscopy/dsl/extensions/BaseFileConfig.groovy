/*
 * -----------------------------------------------------------------------------
 *  Copyright (C) 2019 University of Dundee & Open Microscopy Environment.
 *  All rights reserved.
 *
 *  This program is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; either version 2 of the License, or
 *  (at your option) any later version.
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * ------------------------------------------------------------------------------
 */
package org.openmicroscopy.dsl.extensions

import groovy.transform.CompileStatic
import org.gradle.api.Project
import org.gradle.api.file.ConfigurableFileCollection
import org.gradle.api.provider.Property

@CompileStatic
class BaseFileConfig {

    final String name

    final Project project

    /**
     * Collection of '.ome.xml' files specifying Semantic types
     */
    final ConfigurableFileCollection omeXmlFiles

    /**
     * Template file to pass to VelocityEngine
     */
    final Property<File> template

    BaseFileConfig(String name, Project project) {
        this.name = name
        this.project = project
        this.omeXmlFiles = project.files()
        this.template = project.objects.property(File)
    }

    void omeXmlFiles(Object... files) {
        this.omeXmlFiles.from(files)
    }

    void setOmeXmlFiles(Object... files) {
        this.omeXmlFiles.setFrom(files)
    }

    void template(String template) {
        setTemplate(template)
    }

    void template(File template) {
        setTemplate(template)
    }

    void setTemplate(String t) {
        setTemplate(new File(t))
    }

    void setTemplate(File t) {
        this.template.set(t)
    }

}



