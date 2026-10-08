package gitlet;

import java.io.Serializable;
import java.io.File;
import static gitlet.Utils.*;


public class Blob implements Serializable{
    // The file name
    private String name;

    // The file content
    private byte[] contents;

    // The file content
    private String hash_value;

    /** carete the Blob instatnce from DIR and name */

    public Blob(File DIR, String filename) {
        File file=join(DIR,filename);
        if(!file.exists()){
            throw new GitletException(
                    String.format("File %s does not exist.",filename));
        }
        byte[] contents=readContents(file);
        this.name=filename;
        this.contents=contents;
        this.hash_value=sha1(filename, (Object) contents);
    }

    public Blob(String filename, byte[] contents) {
        this.name=filename;
        this.contents=contents;
        this.hash_value=sha1(filename, (Object) contents);
    }

        public static Blob readBlob(File file){
        if(!file.exists()){
            throw new GitletException(
                    String.format("File does not exist in readBlob."));
        }
        Blob blob=readObject(file,Blob.class);
        return blob;
    }

    public void writeObject_HashValName(File DIR) {
        File file = join(DIR, this.get_Hashval());
        writeObject(file, this);
    }

    public void writeContents_Filename(File DIR) {
        File file = join(DIR, this.name);
        writeContents(file,(Serializable)this.contents);
    }

    public String getName() {
        return this.name;
    }
    public byte[] getcontents() {
        return this.contents;
    }

    public String get_Hashval() {
        return this.hash_value;
    }
}
