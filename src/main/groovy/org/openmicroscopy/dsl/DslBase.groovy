package org.openmicroscopy.dsl

import groovy.transform.CompileStatic
import org.gradle.api.GradleException
import org.gradle.api.file.Directory
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree
import org.gradle.api.file.RegularFile
import org.gradle.api.logging.Logger
import org.gradle.api.logging.Logging
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.util.PatternSet

@SuppressWarnings("UnstableApiUsage")
@CompileStatic
abstract class DslBase {

    private static final Logger Log = Logging.getLogger(DslBase)

    static Provider<Directory> getOutputDirProvider(DirectoryProperty baseDir, Property<File> childDir) {
        childDir.flatMap { File f -> baseDir.dir(f.toString()) }
    }

    static Provider<RegularFile> getOutputFileProvider(DirectoryProperty baseDir, Property<File> childFile) {
        childFile.flatMap { File f -> baseDir.file(f.toString()) }
    }

    static File findDatabaseType(FileCollection collection, String type) {
        if (!type) {
            throw new GradleException("Database type (psql, sql, oracle, etc) not specified")
        }

        File file = new File("$type-types.$FileTypes.EXTENSION_DB_TYPE")
        File databaseType = findInCollection(collection, file, FileTypes.PATTERN_DB_TYPE)
        if (!databaseType) {
            throw new GradleException("Can't find $file in collection of database types")
        }
        Log.info("Found database types file $databaseType")
        return databaseType
    }

    static File findTemplate(FileCollection collection, File file) {
        if (!file) {
            throw new GradleException("No template (.vm) specified")
        }

        if (file.isAbsolute() && file.isFile()) {
            return file
        }
        Log.info("Searching for template with file name: $file")
        File template = findInCollection(collection, file, FileTypes.PATTERN_TEMPLATE)
        if (!template) {
            throw new GradleException("Can't find $file in collection of templates")
        }
        Log.info("Found template file $template")
        return template
    }

    static File findInCollection(FileCollection collection, File file, String include) {
        Set<File> files = getFiles(collection, include)
        Log.info("Looking for file with name $file.name from the following")
        files.find { File f ->
            Log.info("$f")
            f.name == file.name
        }
    }

    static Set<File> getFiles(FileCollection collection, String include) {
        if (collection.isEmpty()) {
            throw new GradleException("Collection is empty")
        }
        FileTree src = collection.asFileTree
        src.matching(new PatternSet().include(include)).files
    }

}
