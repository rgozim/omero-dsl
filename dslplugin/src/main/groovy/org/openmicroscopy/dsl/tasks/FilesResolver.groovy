package org.openmicroscopy.dsl.tasks

import org.gradle.api.Project
import org.gradle.api.file.FileTree
import org.gradle.api.tasks.util.PatternFilterable
import org.gradle.api.tasks.util.PatternSet

class CollectionResolver {

    private final PatternSet patternSet = new PatternSet()

    private final List<Object> files = new ArrayList<>()

    private final Project project

    FileTree getFiles() {
        ArrayList<Object> copy = new ArrayList<Object>(this.files)
        FileTree src = project.files(copy).asFileTree
        return src.matching(patternSet)
    }

    CollectionResolver files(Object... omeXml) {
        Collections.addAll(this.files, omeXml)
        return this
    }

    void setFiles(FileTree source) {
        setOmeXmlFiles((Object) source)
    }

    void setFiles(Object... files) {
        this.files.clear()
        this.files.add(files)
    }



}
