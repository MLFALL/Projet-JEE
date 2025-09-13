package sn.isi.immobilier.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtil {
    private static Properties props = new Properties();

    static {
        try (InputStream input = ConfigUtil.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                throw new RuntimeException("Impossible de trouver config.properties");
            }
            props.load(input);
        } catch (IOException e) {
            throw new RuntimeException("Erreur lors du chargement du fichier config.properties", e);
        }
    }

    public static String get(String key) {
        return props.getProperty(key);
    }
}
