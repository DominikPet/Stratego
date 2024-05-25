package hr.algebra.threerp3.stratego.jndi;

//import com.sun.jndi.fscontext.RefFSContext;
//import com.sun.jndi.fscontext.RefFSContextFactory;

import javax.naming.Binding;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.util.Hashtable;
import java.util.Properties;
import java.util.Scanner;

public class FilesUsage {

    //private static final String INITIAL_CONTEXT_FACTORY = RefFSContextFactory.class.getName();

    //env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContext");

    //private static final String PROVIDER_URL = "file:c:/";

    public static void main(String[] args) {

        Hashtable<String, String> environment = new Hashtable<>();
        environment.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.fscontext.RefFSContextFactory");
        environment.put(Context.PROVIDER_URL, "file:C:/Users/Programer/Desktop/Java 2/stratego - conf");

        try (InitialDirContextCloseable context = new InitialDirContextCloseable(environment)) {
            listBindings(context, "", 1, 2);
            searchBindings(context);

        } catch (NamingException e) {
            e.printStackTrace();
        }
    }

    /*
    private static Hashtable<?, ?> configureEnvironment() {
        return new Hashtable<>() {
            {
                put(Context.INITIAL_CONTEXT_FACTORY, INITIAL_CONTEXT_FACTORY);
                put(Context.PROVIDER_URL, PROVIDER_URL);
            }
        };
    }

     */
    private static void listBindings(Context context, String path, int level, int limit) throws NamingException {
        if (level > limit) {
            return;
        }
        NamingEnumeration<Binding> listBindings = context.listBindings(path);
        while (listBindings.hasMoreElements()) {
            Binding binding = listBindings.next();
            //System.out.println(binding);
            System.out.printf("%" + level + "s", " ");
            System.out.println(binding.getName());

            /*
            if (binding.getClassName().equals(RefFSContext.class.getName())) {
                listBindings(context, path + "/" + binding.getName(), level + 1, limit);
            }

             */
        }
    }

    private static void searchBindings(Context context) {
        System.out.print("Insert filename: ");
        String fileName = readFilename(System.in);

        try {
            Object object = context.lookup(fileName);
            Properties props = new Properties();
            props.load(new FileReader(object.toString()));
            System.out.println("The property server.port = " + props.getProperty("server.port"));
            System.out.println("The property client.port = " + props.getProperty("client.port"));
            System.out.println("The property host = " + props.getProperty("host"));
            System.out.println("The property random.port.hint = " + props.getProperty("random.port.hint"));
            System.out.println("The property rmi.port = " + props.getProperty("rmi.port"));
        } catch (NamingException | IOException ex) {
            ex.printStackTrace();
        }

        /*
        // if no exception is thrown, the filename exists - if it is directory, list its contents, otherwise print the name
        if (object instanceof Context) {
            listBindings((Context) object, "", 1, 2);
        } else {
            System.err.println(fileName);
        }

         */
    }

    private static String readFilename(InputStream in) {
        try (Scanner scanner = new Scanner(in)) {
            return scanner.next();
        }
    }
}

