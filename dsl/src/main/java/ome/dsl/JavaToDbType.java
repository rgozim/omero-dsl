package ome.dsl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Be mindful that this is a Singleton class.
 * It's purpose is to map a small number of Java types (such as string[]) to
 * the equivalent type in PSQL, or any other database engine/language.
 */
public class JavaToDbType {
    private static final Logger Log = LoggerFactory.getLogger(JavaToDbType.class);
    private static final Object mLock = new Object();
    private static Properties databaseTypes;

    public static Properties getInstance(String profile) {
        synchronized (mLock) {
            if (databaseTypes == null) {
                databaseTypes = new Properties();
                try {
                    databaseTypes.load(getPropertiesFile(profile));
                } catch (IOException e) {
                    Log.error("Can't open" + profile + "-type.properties file!", e);
                }
            }
            return databaseTypes;
        }
    }

    private static InputStream getPropertiesFile(String profile) {
        return JavaToDbType.class.getClassLoader()
                .getResourceAsStream("properties/" + profile + "-types.properties");
    }
}
