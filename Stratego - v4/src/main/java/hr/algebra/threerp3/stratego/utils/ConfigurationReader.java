package hr.algebra.threerp3.stratego.utils;

import hr.algebra.threerp3.stratego.exceptions.InvalidConfigurationKeyNameException;
import hr.algebra.threerp3.stratego.jndi.InitialDirContextCloseable;
import hr.algebra.threerp3.stratego.model.ConfKey;

import javax.naming.Context;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Properties;

public class ConfigurationReader {

    private static final Hashtable<String, String> environment;

    private static ConfigurationReader reader;

    static {
        environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, "file:C:\\Users\\Programer\\Desktop\\Java 2\\stratego - conf");
    }

    public static String readStringConfigurationValueForKey(ConfKey key) {
        try (InitialDirContextCloseable context = new InitialDirContextCloseable(environment)) {
            return readValueForKey(context, key);
        } catch (NamingException e) {
            throw new RuntimeException(e);
        }
    }


    public static Integer readIntegerConfigurationValueForKey(ConfKey key) {
        String value = readStringConfigurationValueForKey(key);
        return Integer.parseInt(value);
    }

    private static String readValueForKey(Context context, ConfKey key) {
        String fileName = "conf.properties";

        try {
            Object object = context.lookup(fileName);
            Properties props = new Properties();
            props.load(new FileReader(object.toString()));
            String value = props.getProperty(key.getKeyName());
            if (value == null) {
                throw new InvalidConfigurationKeyNameException("The conf key " + key.getKeyName() + " does not exist");
            }
            return props.getProperty(key.getKeyName());
        } catch (NamingException | IOException ex) {
            throw new RuntimeException("The conf can't be read", ex);
        }
    }
}
