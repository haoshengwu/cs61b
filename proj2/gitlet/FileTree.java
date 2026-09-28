package gitlet;
import java.io.Serializable;
import java.util.*;

public class FileTree extends TreeMap<String, String> implements Serializable {
    FileTree() {
        super();
    }

    /**
     * key is for the file name and value is the hash value
     *
     * @return
     */
    public String put(String key, String value) {
        super.put( key, value);
        return key;
    }

    /** return the hash value for the file name */
    public String get(String key) {
        return (String) super.get(key);
    }

    /** remove the key */
    public void remove(String key) {
        super.remove(key);
    }
}
