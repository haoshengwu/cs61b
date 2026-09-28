package gitlet;

import java.io.File;

import static gitlet.Blob.*;
import static gitlet.Commit.*;
import static gitlet.CommitTree.*;
import static gitlet.Utils.*;


import java.util.*;


// TODO: any imports you need here

/** Represents a gitlet repository.
 *  TODO: It's a good idea to give a description here of what else this Class
 *  does at a high level.
 *
 *  @author TODO
 */
public class Repository {
    /**
     * TODO: add instance variables here.
     *
     * List all instance variables of the Repository class here with a useful
     * comment above them describing what that variable represents and how that
     * variable is used. We've provided two examples for you.
     */

    /** The current working directory. */
    public static final File CWD = new File(System.getProperty("user.dir"));
    /** The .gitlet directory. */
    public static final File GITLET_DIR = join(CWD, ".gitlet");

    /** The STARGE directory. */
    public static final File STAGE_DIR = join(GITLET_DIR, "Stage");
    /** The file index in stage direction*/
    public static final File STAGE_INDEX = join(STAGE_DIR, "stageindex");


    public static final File BLOB_DIR = join(GITLET_DIR, "Blob");
    public static final File COMMIT_DIR = join(GITLET_DIR, "Commit");
    /** The initial commit file*/
    public static final File INIT_COMMIT = join(COMMIT_DIR, "InitCommit");

    public static final File COMMITTREE_FILE = join(COMMIT_DIR, "commitTree");



    /* TODO: fill in the rest of this class. */

    /** init command */
    public static void initCommand(){
        if(GITLET_DIR.exists()) {
            System.out.println("A Gitlet version-control system already exists in the current directory.");
            return;
        }
        /* Create folders */
        else{
            GITLET_DIR.mkdirs();
            BLOB_DIR.mkdirs();
            COMMIT_DIR.mkdirs();
            STAGE_DIR.mkdirs();
        }
        /* Create initial commit */
        Date timestamp = new Date(0);
        //System.out.println(timestamp);
        Commit init_commit=new Commit("initial commit",timestamp,null,null, null);
        writeObject(INIT_COMMIT, init_commit);

        /* Create stage index */
        FileHashMap stageindex= new FileHashMap();
        writeObject(STAGE_INDEX,stageindex);

        /* Create commitTree */
        CommitTree commitTree=new CommitTree("InitCommit");
        commitTree.write_CommitTree(COMMITTREE_FILE);
//        writeObject(COMMITTREE_FILE, commitTree);
    }

    /** add command */
    public static void addCommand(String filename){

        /* 1. Check whether this file exist or not*/
        final File TARGETFILE = join(CWD, filename);
        if(!TARGETFILE.exists()){
            throw new GitletException(
                    String.format("File does not exist."));
        }

        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);


        /* Creat the blob */
        Blob newblob = new Blob(CWD, filename);

        /* get current commit */
        //System.out.printf(get_CommitStr_from_head());
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);

        Commit curr_commit = Commit.readCommit(join(COMMIT_DIR, commitTree.getHeaddata()));

        /** Compare hash value of tobeadd file with the one in current commit */
        String s1= curr_commit.get_fileTree_hash_value(filename);
        String s2= newblob.get_Hashval();
        //System.out.printf("newblob hash value: %s\n", s2);

        if(s1!=null && s1.equals(s2)){
            stageindex.remove(filename);
            File file = join(STAGE_DIR,filename);
            if(!file.exists()){
                file.delete();
            }
        } else {
            if(stageindex.containsKey(filename)){
                File file = join(STAGE_DIR,filename);
                file.delete();
            }
            writeObject(join(STAGE_DIR, s2), newblob);
            stageindex.put(filename,s2);
        }
        writeObject(STAGE_INDEX,stageindex);
        return;
    }

    /** commit command */
    public static void commit(String message) {

        //Read stage index
        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);

        if(stageindex.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(1);
        }

        //Check message
        if(message == null || message.isEmpty()){
//            throw new GitletException(
//                    String.format("File does not exist."));
            System.out.println("File does not exist.");
            System.exit(2);
        }

        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);

        //Generate new commit based on head and update the treemap based on stageindex
        String parentID=commitTree.getHeaddata();
        Commit commit = generate_commit_from_old(COMMIT_DIR,message,new Date(),parentID,stageindex);
        commit.writeObject_HashValName(COMMIT_DIR);

        //update commitTree and also head
        commitTree.add_node_from_head(commit.commit_HashVal());
        commitTree.write_CommitTree(COMMITTREE_FILE);

        //write as blobs
        for(String key : stageindex.keySet()){
            String value = stageindex.get(key);
            if(!value.equals("removal")){
                //do nothing
                Blob blob=readBlob(join(STAGE_DIR,value));
                blob.writeObject_HashValName(BLOB_DIR);
            }else {
                //TO DO
            }
        }

        //Empty stageindex and write
        stageindex.clear();
        writeObject(STAGE_INDEX,stageindex);

        //delete files in STAGE_DIR
        List<String> list=plainFilenamesIn(STAGE_DIR);
        if(!list.isEmpty()){
            for(String key : list){
                if(!key.equals("stageindex")){
                    File file = join(STAGE_DIR,key);
                    file.delete();
                }
            }
        }
    }

    /** rm command */
    public static void rm(String filename){
        //Read stageindex and commitTree
        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        Commit head=Commit.readCommit(join(COMMIT_DIR,commitTree.getHeaddata()));

        //Blob name (Hash value) from head file tree
        String s1= head.get_fileTree_hash_value(filename);

        //Blob name (Hash value) from stageindex tree
        String s2= stageindex.get(filename);

        if(s1 == null && s2 == null){
            System.out.println("No reason to remove the file.");
            System.exit(3);
        }

        //Unstage
        if(s1 != null){
            File file = join(STAGE_DIR, stageindex.get(filename));
            file.delete();
            stageindex.remove(filename);
        }

        if(s2!=null){
            stageindex.put(filename,"removal");
            File file = join(CWD,filename);
            if(!file.exists()){
                restrictedDelete(file);
            }
        }
        return;
    }

    /** log command */
    public static void log() {
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        log_node(commitTree.getHead());
    }

    //commit filename is hash value
    private static void log_commit(String filename){
        System.out.println("===");
        System.out.println("commit "+filename);
        Commit commit=Commit.readCommit(join(COMMIT_DIR,filename));
        Date date=commit.get_timestamp();
        String parentID=commit.get_parentID();
        String secondparentID=commit.get_secondparentID();
        if(secondparentID!=null){
            System.out.println("Merge: "+ parentID.substring(0, 7) + secondparentID.substring(0, 7));
        }
        String customDate = new Formatter()
                .format("%1$ta %1$tb %1$te %1$tT %1$tY %1$tz", date)
                .toString();
        System.out.println(customDate);
        System.out.println(commit.get_message());
        System.out.println();
    }

    private static void log_node(Node node){
        String hashval=node.get_node_data();
        log_commit(hashval);
        Node parent = node.get_node_parent();
        if(parent == null){
            return;
        } else {
            log_node(parent);
        }
    }

    /** global-log commad */
    public static void global_log() {
        //delete files in STAGE_DIR
        List<String> list=plainFilenamesIn(COMMIT_DIR);
        if(!list.isEmpty()){
            for(String key : list){
                if(!key.equals("commitTree")){
                    log_commit(key);
                }
            }
        }
    }

    /** find command */
    public static void find(String message) {
        boolean found=false;
        List<String> list=plainFilenamesIn(COMMIT_DIR);
        if(!list.isEmpty()){
            for(String key : list){
                if(!key.equals("commitTree")){
                    Commit commit=Commit.readCommit(join(COMMIT_DIR,key));
                    String message1=commit.get_message();
                    if(message.contains(message1)){
                        found=true;
                    }
                    System.out.println(key);
                }
            }
        }
        if(!found){
            System.out.println("Found no commit with that message.");
            System.exit(4);
        }

    }

    /** status command */
    public static void status() {
        /** Read commit tree */
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        ArrayList<String> branchsArrList= new ArrayList<String>();
        HashMap<String,Node> branchs = commitTree.getBranches();
        Node head=branchs.get("head");
        for(String key : branchs.keySet()){
            branchsArrList.add(key);
        }
        Collections.sort(branchsArrList);

        /** print branch */
        System.out.println("=== Branches ===");
        for(String branch : branchsArrList){
            if(branch.equals(commitTree.get_cur_branch())){
                System.out.println("*"+branch);
            }else {
                System.out.println(branch);
            }
        }
        System.out.println();

        /** Read stage index */
        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);
        ArrayList<String> stageArrList= new ArrayList<String>();
        ArrayList<String> removeArrList= new ArrayList<String>();

        for(String key : stageindex.keySet()){
            String val=stageindex.get(key);
            if(val.equals("removal")){
                removeArrList.add(key);
            }else {
                stageArrList.add(key);
            }
        }
        Collections.sort(stageArrList);
        Collections.sort(removeArrList);

        /** print staged file */
        System.out.println("=== Staged Files ===");
        for(String filename : stageArrList){
            System.out.println(filename);
        }
        System.out.println();

        /** print removed file */
        System.out.println("=== Removed Files ===");
        for(String filename : removeArrList){
            System.out.println(filename);
        }
        System.out.println();

        /** To Do **/
        System.out.println("=== Modifications Not Staged For Commit ===");
        System.out.println();

        /** To Do **/
        System.out.println("=== Untracked Files ===");
        System.out.println();
    }

    private static void write_file_from_commit(Commit commit, String filename){
        /** DEBUG */

        //System.out.println(filename);
        FileTree fileTree=commit.get_file_tree();
        for( String key : fileTree.keySet()){
            System.out.println(key+": "+fileTree.get(key));
        }

        String blobId=commit.get_fileTree_hash_value(filename);
//        System.out.println("blobId: "+blobId);
        if(blobId==null){
            System.out.println("File does not exist in that commit.");
            System.exit(5);
        }
        Blob blob = Blob.readBlob(join(BLOB_DIR,blobId));
        blob.writeContents_Filename(CWD);
        //File file = join(CWD,blob.getName());
        //writeContents(file, (Object) blob.getContents());
    }

    public static void checkout(String[] arg) {
        System.out.println(arg.length);

        if(arg.length == 3 && (arg[1].equals("--"))){
            CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
            String headcommitID=commitTree.getHeaddata();
            Commit commit=Commit.readCommit(join(COMMIT_DIR,headcommitID));
            write_file_from_commit(commit, arg[2]);
        } else if (arg.length == 4 && (arg[2].equals("--"))) {
            File commitfile=join(COMMIT_DIR,arg[1]);
            if(commitfile.exists()){
                Commit commit=Commit.readCommit(commitfile);
                write_file_from_commit(commit,arg[3]);
            }else{
                System.out.println("No commit with that id exists.");
                System.exit(6);
            }
        } else if (arg.length == 2 ) {
            CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
            HashMap<String,Node> branchs = commitTree.getBranches();
            if(branchs.containsKey(arg[1])){
                Node branch=branchs.get(arg[1]);
                String commitID=branch.get_node_data();
                Commit commit=Commit.readCommit(join(COMMIT_DIR,commitID));
                FileTree fileTree=commit.get_file_tree();
                for (String key : fileTree.keySet()){
                    //Check whether the file has been tracked.
                    Blob blob_for_check=new Blob(CWD,key);
                    String val=blob_for_check.get_Hashval();
                    File file_for_check=join(COMMIT_DIR,val);
                    if(!file_for_check.exists()){
                        System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                        System.exit(5);
                    }else{
                        write_file_from_commit(commit,key);
                    }
                }
            } else if ((arg[1].equals(commitTree.get_cur_branch()))){
                System.out.println("No need to checkout the current branch.");
                System.exit(6);
            } else {
                System.out.println("No such branch exists.");
                System.exit(7);
            }
        } else {
            System.out.println("Unexpected argument.");
            System.exit(8);
        }

    }

}
