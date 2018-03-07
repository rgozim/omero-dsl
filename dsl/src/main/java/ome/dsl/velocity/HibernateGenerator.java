package ome.dsl.velocity;

import ome.dsl.SemanticType;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;

import java.io.File;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;

public class HibernateGenerator extends Generator {

    /**
     * Folder to write velocity generated content
     */
    private File outputFile;

    private HibernateGenerator(Builder builder) {
        this.profile = builder.profile;
        this.omeXmlFiles = builder.omeXmlFiles;
        this.template = builder.template;
        this.outputFile = builder.outputFile;
        this.velocity.init(builder.properties);
    }

    @Override
    public void run() {
        // Create list of semantic types from source files
        List<SemanticType> types = loadSemanticTypes(omeXmlFiles);
        if (types.isEmpty()) {
            return; // Skip when no files, otherwise we overwrite.
        }

        // Sort types by short name
        types.sort(Comparator.comparing(SemanticType::getShortname));

        // Get the template file
        Template t = velocity.getTemplate(findTemplate());

        // Velocity process the semantic types
        for (SemanticType st : types) {
            VelocityContext vc = new VelocityContext();
            vc.put("types", st);

            writeToFile(vc, t, prepareOutput(outputFile));
        }
    }

    public static class Builder {
        String profile;
        File template;
        File outputFile;
        List<File> omeXmlFiles;
        Properties properties;

        public Builder setProfile(String profile) {
            this.profile = profile;
            return this;
        }

        public Builder setOmeXmlFiles(List<File> omeXmlFiles) {
            this.omeXmlFiles = omeXmlFiles;
            return this;
        }

        public Builder setTemplate(File template) {
            this.template = template;
            return this;
        }

        public Builder setOutput(File outputFile) {
            this.outputFile = outputFile;
            return this;
        }

        public Builder setVelocityProperties(Properties p) {
            this.properties = p;
            return this;
        }

        public Generator build() {
            return new HibernateGenerator(this);
        }
    }
}
