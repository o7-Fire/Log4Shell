package itzbenz.payload;

import java.io.Serializable;

public class ObjectPayloadSerializable implements Serializable {

    static {
        General.init();
    }

    protected final String name;

    public ObjectPayloadSerializable(String name) {
        this.name = name;
        System.out.println(this);
    }

    @Override
    public String toString() {
        return "My name is " + name;
    }
}
