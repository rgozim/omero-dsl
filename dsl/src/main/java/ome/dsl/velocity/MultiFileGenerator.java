package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.security.InvalidParameterException;
import java.util.Collection;
import java.util.function.Function;

public class MultiFileGenerator extends Generator {

    /**
     * Folder to write velocity generated content
     */
    private File outputDir;

    /**
     * callback for formatting output file name
     */
    private Function<SemanticType, String> fileNameFormatter;

    private MultiFileGenerator(Builder builder) {
        super(builder);
        if (builder.outputDir == null) {
            throw new InvalidParameterException("Where are files supposed to be written to?");
        }

        if (builder.fileNameFormatter == null) {
            throw new InvalidParameterException("File name formatter is required");
        }

        outputDir = builder.outputDir;
        fileNameFormatter = builder.fileNameFormatter;
    }

    public void run() {
        // Create list of semantic types from source files
        Collection<SemanticType> types = loadSemanticTypes(omeXmlFiles);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("type", st);

            // Format the final filename using callback
            String filename = fileNameFormatter.apply(st);
            parseTemplate(vc, template, new File(outputDir, filename));
        }
    }

    public static class Builder extends Generator.Builder {
        private File outputDir;
        private Function<SemanticType, String> fileNameFormatter;

        public Builder setOutputDir(File outputDir) {
            this.outputDir = outputDir;
            return this;
        }

        public Builder setFileNameFormatter(Function<SemanticType, String> callback) {
            this.fileNameFormatter = callback;
            return this;
        }

        public MultiFileGenerator build() {
            return new MultiFileGenerator(this);
        }
    }
}
