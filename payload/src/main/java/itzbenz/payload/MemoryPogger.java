package itzbenz.payload;

import java.io.Serializable;

public class MemoryPogger implements Serializable {
    public static long[][] pogger;
    
    static {
        init();
    }
    
    public static long getMemoryUsage() {
        try {
            return Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory();
        }catch(Throwable t){
            return 0;
        }
    }
    
    public static String toHumanReadable(long bytes) {
        if (bytes < 1024){
            return bytes + " bytes";
        }
        if (bytes < 1024 * 1024){
            return (bytes / 1024) + " KB";
        }
        if (bytes < 1024 * 1024 * 1024){
            return (bytes / (1024 * 1024)) + " MB";
        }
        return (bytes / (1024 * 1024 * 1024)) + " GB";
    }
    
    public static void init() {
        long before = getMemoryUsage();
        General.log("Memory usage: " + toHumanReadable(before));
        int size = (int) 1e4;//765 MB
        try {
            pogger = new long[size][size];
        }catch(Throwable t){
            General.log("Fail: " + t.getMessage());
        }
        long after = getMemoryUsage();
        General.log("Memory usage: " + toHumanReadable(after));
        General.log("Memory usage difference: " + toHumanReadable(after - before));
    }
    
    public static void main(String[] args) {
    
    }
}
