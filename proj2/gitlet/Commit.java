package gitlet;

// TODO: any imports you need here

import java.io.ByteArrayOutputStream;
import java.io.ObjectOutputStream;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.SplittableRandom;

import static gitlet.Utils.*;

/** Represents a gitlet commit object.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Commit implements Serializable{
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Commit class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided one example for `message`.
     */

    /** The message of this Commit. */
    private String message;
    //private String author;
    private Date timestamp ;
    private String parentID;
    private String secondparentID;
    private FileTree fileTree;

    /* TODO: fill in the rest of this class. */
    public Commit(String message, Date timestamp, String parentID, String secondparentID, FileTree fileTree) {
        this.message = message;
        this.timestamp  = timestamp;
        this.parentID = parentID;
        this.secondparentID = secondparentID;
        if(fileTree == null) {
            this.fileTree = new FileTree();
        } else {
            this.fileTree = fileTree;
        }
    }

    public static Commit readCommit(File file) {
        if(!file.exists()){
            throw new GitletException(
                    String.format("File %s does not exist in readCommit."));
        }
        Commit commit = null;
        commit=readObject(file,Commit.class);
        return commit;
    }

    public FileTree get_file_tree(){
        return this.fileTree;
    }

    public String get_message(){
        return this.message;
    }

    public Date get_timestamp(){
        return this.timestamp;
    }

    public String get_parentID(){
        return this.parentID;
    }

    public String get_secondparentID(){
        return this.secondparentID;
    }

    public String get_fileTree_hash_value(String key){
        if(this.fileTree==null){
            return null;
        }
        return this.fileTree.get(key);
    }

    public static Commit generate_commit_from_old(File DIR,
                                           String message, Date timestamp, String parentID,
                                           String secondparentID,
                                           HashMap<String, String> stageindex){
        File file=join(DIR, parentID);
        Commit newcommit = readCommit(file);
        newcommit.message=message;
        newcommit.timestamp = timestamp;
        newcommit.parentID = parentID;
        newcommit.secondparentID = secondparentID;
        newcommit.update_commit_filetree(stageindex);
        return newcommit;
    }

    private void update_commit_filetree(HashMap<String, String> stageindex){
        if (this.fileTree==null){
            this.fileTree=new FileTree();
        }
        for(String key : stageindex.keySet()){
            String value = stageindex.get(key);
            if(value.equals("removal")){
                this.fileTree.remove(key);
            }else{
                this.fileTree.put(key, value);
            }
        }
    }

    public void writeObject_HashValName(File DIR){
        String hashval=sha1((Object) this.convert_to_byte());
        File file = join(DIR, hashval);
        writeObject(file,(Serializable)this);
    }

    public String commit_HashVal(){
        String hashval=sha1((Object) this.convert_to_byte());
        return hashval;
    }

    private byte[] convert_to_byte() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try (ObjectOutputStream out = new ObjectOutputStream(bos)) {
            out.writeObject(this);
            out.flush();
            return bos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public void print_filetree(){
        for(String key : this.fileTree.keySet()){
            System.out.println(key+": "+this.fileTree.get(key));
        }
    }
}
