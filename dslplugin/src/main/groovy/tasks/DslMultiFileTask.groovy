package tasks

import ome.dsl.SemanticType
import ome.dsl.velocity.Generator
import ome.dsl.velocity.MultiFileGenerator
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.PathSensitive
import org.gradle.api.tasks.PathSensitivity

class DslMultiFileTask extends DslBaseTask {

    @Internal
    MultiFileGenerator.FileNameFormatter formatOutput

    /**
     * Set this when you want to generate multiple files
     * Note: also requires setting {@link this.formatOutput}
     */
    @OutputDirectory
    @PathSensitive(PathSensitivity.ABSOLUTE)
    File outputPath

    void setFormatOutput(Closure formatter) {
        formatOutput = new MultiFileGenerator.FileNameFormatter() {
            @Override
            String format(SemanticType t) {
                return formatter(t)
            }
        }
    }

    void formatOutput(Closure formatter) {
        setFormatOutput(formatter)
    }

    void setOutputPath(File path) {
        this.outputPath = setAbsPath(path)
    }

    void outputPath(File path) {
        setOutputPath(path)
    }

    @Override
    protected Generator.Builder createGenerator() {
        return new MultiFileGenerator.Builder()
                .setOutputDir(outputPath)
                .setFileNameFormatter(formatOutput)
    }
}
