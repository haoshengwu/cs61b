package gitlet;

import java.io.File;

import static gitlet.Blob.*;
import static gitlet.Commit.*;
import static gitlet.CommitTree.*;
import static gitlet.Utils.*;


import java.text.SimpleDateFormat;
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
    //public static final File INIT_COMMIT = join(COMMIT_DIR, "InitCommit");

    public static final File COMMITTREE_FILE = join(COMMIT_DIR, "commitTree");

    private static void check_GITLET_DIR(){
        if(!GITLET_DIR.exists()){
            System.out.println("Not in an initialized Gitlet directory.");
            System.exit(0);
        }
    }

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
        init_commit.writeObject_HashValName(COMMIT_DIR);

        /* Create stage index */
        FileHashMap stageindex= new FileHashMap();
        writeObject(STAGE_INDEX,stageindex);

        /* Create commitTree */
        CommitTree commitTree=new CommitTree(init_commit.commit_HashVal());
        commitTree.write_CommitTree(COMMITTREE_FILE);
//        writeObject(COMMITTREE_FILE, commitTree);
    }

    /** add command */
    public static void addCommand(String filename){
        check_GITLET_DIR();
        /* 1. Check whether this file exist or not*/
        final File TARGETFILE = join(CWD, filename);
        if(!TARGETFILE.exists()){
            System.out.println("File does not exist.");
            System.exit(0);
        }

        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);


        /* Creat the blob */
        Blob newblob = new Blob(CWD, filename);

        /* get current commit */
        //System.out.printf(get_CommitStr_from_head());
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);

        Commit curr_commit = readCommit(join(COMMIT_DIR, commitTree.getHeaddata()));

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
        check_GITLET_DIR();
        //Read stage index
        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);

        if(message == null || message.isEmpty()){
            System.out.println("Please enter a commit message.");
            System.exit(0);
        }

        if(stageindex.isEmpty()){
            System.out.println("No changes added to the commit.");
            System.exit(0);
        }

        //Check message
        commit_merge(message,null);
    }



    private static void commit_merge(String message, Node secondparentnode) {

        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);

        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);

        //Generate new commit based on head and update the treemap based on stageindex
        String parentID=commitTree.getHeaddata();
        Commit commit=null;
        if(secondparentnode!=null){
            commit = generate_commit_from_old(COMMIT_DIR,message,new Date(),parentID,secondparentnode.get_node_data(),stageindex);
        }
        else {
            commit = generate_commit_from_old(COMMIT_DIR,message,new Date(),parentID,null,stageindex);
        }

        commit.writeObject_HashValName(COMMIT_DIR);

        //update commitTree and also head
        commitTree.add_node_from_head(commit.commit_HashVal());

//        if(secondparentnode!=null){
//            secondparentnode.add_child(commitTree.getHead());
//        }
        commitTree.write_CommitTree(COMMITTREE_FILE);
        //System.out.println("current branch commit: " + commitTree.get_cur_branch_node().get_node_data());
        //write as blobs
        //commitTree.print_commitTree();
        //commitTree.print_commitTree_branches();

        for(String key : stageindex.keySet()){
            String value = stageindex.get(key);
            if(!value.equals("removal")){
                //do nothing
                Blob blob=readBlob(join(STAGE_DIR,value));
                blob.writeObject_HashValName(BLOB_DIR);
            }
        }

        //Empty stageindex and write
        stageindex.clear();
        writeObject(STAGE_INDEX,stageindex);

        //delete files in STAGE_DIR
        deleteStagedFiles();
    }

    private static void deleteStagedFiles(){
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
        check_GITLET_DIR();
        //Read stageindex and commitTree
        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        Commit head= readCommit(join(COMMIT_DIR,commitTree.getHeaddata()));

        //Blob name (Hash value) from head file tree
        String s1= head.get_fileTree_hash_value(filename);

        //Blob name (Hash value) from stageindex tree
        String s2= stageindex.get(filename);
//        System.out.println(s1);
//        System.out.println(s2);
        if(s1 == null && s2 == null){
            System.out.println("No reason to remove the file.");
            System.exit(0);
        }

        //Unstage
        if(s2 != null){
            File file = join(STAGE_DIR, stageindex.get(filename));
            file.delete();
            stageindex.remove(filename);
        }

        if(s1 != null){
            stageindex.put(filename,"removal");
            File file = join(CWD,filename);
            if(file.exists()){
                restrictedDelete(file);
            }
        }
        writeObject(STAGE_INDEX,stageindex);

        return;
    }

    /** log command */
    public static void log() {
        check_GITLET_DIR();
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        log_node(commitTree.getHead());
    }

    //commit filename is hash value
    private static void log_commit(String filename){
        System.out.println("===");
        System.out.println("commit "+filename);
        Commit commit= readCommit(join(COMMIT_DIR,filename));
        Date date=commit.get_timestamp();
        String parentID=commit.get_parentID();
        String secondparentID=commit.get_secondparentID();
        if(secondparentID!=null){
            System.out.println("Merge: "+ parentID.substring(0, 7) +  " " + secondparentID.substring(0, 7));
        }
        SimpleDateFormat dateFormat =
                new SimpleDateFormat("EEE MMM d HH:mm:ss yyyy Z", Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
        System.out.println("Date: " + dateFormat.format(date));

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
        check_GITLET_DIR();
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
        check_GITLET_DIR();
        boolean found=false;
        List<String> list=plainFilenamesIn(COMMIT_DIR);
        if(!list.isEmpty()){
            for(String key : list){
                if(!key.equals("commitTree")){
                    Commit commit= readCommit(join(COMMIT_DIR,key));
                    String message1=commit.get_message();
                    //System.out.println(message1);
                    if(message1.contains(message)){
                        found=true;
                        System.out.println(key);
                    }

                }
            }
        }
        if(!found){
            System.out.println("Found no commit with that message.");
            System.exit(0);
        }

    }

    /** status command */
    public static void status() {
        check_GITLET_DIR();
        /** Read commit tree */
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        ArrayList<String> branchsArrList= new ArrayList<String>();
        HashMap<String,Node> branchs = commitTree.getBranches();
        Node head=branchs.get("head");
        for(String key : branchs.keySet()){
            //System.out.println(key);
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
//        FileTree fileTree=commit.get_file_tree();
//        for( String key : fileTree.keySet()){
//            System.out.println(key+": "+fileTree.get(key));
//        }

        String blobId=commit.get_fileTree_hash_value(filename);
//        System.out.println("blobId: "+blobId);
        if(blobId==null){
            System.out.println("File does not exist in that commit.");
            System.exit(0);
        }
        Blob blob = readBlob(join(BLOB_DIR,blobId));
        blob.writeContents_Filename(CWD);
        //File file = join(CWD,blob.getName());
        //writeContents(file, (Object) blob.getContents());
    }

    /** checkout command */


    public static void checkout(String[] arg) {
        //System.out.println(arg.length);
        check_GITLET_DIR();
        if(arg.length == 3){
            if(!arg[1].equals("--")){
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
            String headcommitID=commitTree.getHeaddata();
            Commit commit= readCommit(join(COMMIT_DIR,headcommitID));
            write_file_from_commit(commit, arg[2]);
        } else if (arg.length == 4 ) {
            if(!arg[2].equals("--")){
                System.out.println("Incorrect operands.");
                System.exit(0);
            }
            String id = "";
            CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);

            if(arg[1].length()<40){
                id=commitTree.get_full_commitID(arg[1]);
            }
            //System.out.println(id);
            File commitfile=join(COMMIT_DIR,id);
            if(commitfile.exists()){
                Commit commit= readCommit(commitfile);
                write_file_from_commit(commit,arg[3]);
            }else{
                System.out.println("No commit with that id exists.");
                System.exit(0);
            }
        } else if (arg.length == 2 ) {
            CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
            HashMap<String,Node> branchs = commitTree.getBranches();

            if ((arg[1].equals(commitTree.get_cur_branch()))){
                System.out.println("No need to checkout the current branch.");
                System.exit(0);
            }

            if(branchs.containsKey(arg[1])){
                Node branch=branchs.get(arg[1]);
                String commitID=branch.get_node_data();
                Commit commit= readCommit(join(COMMIT_DIR,commitID));
                commitTree.set_cur_branch_name(arg[1]);
                checkout_commit(commit);
                //commitTree.set_cur_branch_node_to_commitID(commitID);
                commitTree.set_head_by_commitID(commitID);
                commitTree.write_CommitTree(COMMITTREE_FILE);

                //Empty stageindex, write stageindex and clear staged area
                FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);
                stageindex.clear();
                writeObject(STAGE_INDEX,stageindex);
                deleteStagedFiles();
            } else {
                System.out.println("No such branch exists.");
                System.exit(0);
            }
        } else {
            System.out.println("Unexpected argument.");
            System.exit(0);
        }
    }

    private static void checkout_commit(Commit commit){
        FileTree fileTree=commit.get_file_tree();
        List<String> list=plainFilenamesIn(CWD);
        //Check all files in CWD
        for(String filename : list){
            //check whether there is blob
            Blob blob_for_check=new Blob(CWD,filename);
            String val=blob_for_check.get_Hashval();
            File file_for_check=join(BLOB_DIR,val);
            //if not commit, then error
            if(!file_for_check.exists()){
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
            //if in the
            if(fileTree==null){
                File file=(join(CWD,filename));
                file.delete();
            } else if(!fileTree.containsKey(filename)){
                File file=(join(CWD,filename));
                file.delete();
            }
        }
        if(fileTree!=null){
            for(String key : fileTree.keySet()){
                Blob blot_to_write= readBlob(join(BLOB_DIR,fileTree.get(key)));
                blot_to_write.writeContents_Filename(CWD);
            }
        }
    }


    /** branch command */
    public static void branch(String branchname) {
        check_GITLET_DIR();
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        if(commitTree.branch_exist(branchname)){
            System.out.println("A branch with that name already exists.");
            System.exit(0);
        }
        commitTree.add_branch(branchname);
        commitTree.write_CommitTree(COMMITTREE_FILE);
    }

    /** rm-branch command */
    public static void rm_branch(String branchname) {
        check_GITLET_DIR();
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        if(!commitTree.branch_exist(branchname)){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if(commitTree.get_cur_branch().equals(branchname)){
            System.out.println("Cannot remove the current branch.");
            System.exit(0);
        }
        commitTree.remove_branch(branchname);
        commitTree.write_CommitTree(COMMITTREE_FILE);
    }


    /** reset command */
    public static void reset(String commitID){
        check_GITLET_DIR();
        //check and read commit
        File commitfile=join(COMMIT_DIR,commitID);
        FileHashMap stageindex= readObject(STAGE_INDEX, FileHashMap.class);

        if(!commitfile.exists()){
            System.out.println("No commit with that id exists.");
            System.exit(0);
        }
        Commit current_commit= readCommit(commitfile);
        FileTree fileTree=current_commit.get_file_tree();

        List<String> list=plainFilenamesIn(CWD);
        //delete file not in the commit
        for(String filename : list) {
            if(!fileTree.containsKey(filename)){
                File file=join(CWD,filename);
                file.delete();
            }
        }

        //Read commitTree
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);

        checkout_commit(current_commit);

        //Delete Starge
        stageindex.clear();
        writeObject(STAGE_INDEX,stageindex);
        deleteStagedFiles();

        //Set current branch head
        commitTree.set_cur_branch_node_to_commitID(commitID);
        commitTree.set_head_by_commitID(commitID);

        commitTree.write_CommitTree(COMMITTREE_FILE);
    }


    //return the split point for two node in the commitTree

    private static Node split_point(Node node1, Node node2){
        HashMap<String,Node> map=new HashMap<>();
        Node current_node=node1;

        while(current_node != null){
            //System.out.println("node1 "+current_node.get_node_data());
            map.put(current_node.get_node_data(),current_node);
            current_node=current_node.get_node_parent();
        }

        Node node_tmp=node2;
        while(node_tmp != null){
            //System.out.println("node2 "+ node_tmp.get_node_data());
            if(map.containsKey(node_tmp.get_node_data())){
                return node_tmp;
            }
            node_tmp=node_tmp.get_node_parent();
        }
        return node_tmp;
    }

    public static Node last_split(Node node){
        Node cur = node;
        while(cur!=null){
            if(cur.get_child_count()>1){
                return  node;
            }
            else {
                cur=cur.get_node_parent();
            }
        }
        return null;
    }

    /** merge command */
    public static void merge(String branchname) {
        check_GITLET_DIR();
        CommitTree commitTree = read_CommitTree(COMMITTREE_FILE);
        //commitTree.print_commitTree();

        //commitTree.print_commitTree_branches();
        FileHashMap stageindex = readObject(STAGE_INDEX, FileHashMap.class);
        if(!stageindex.isEmpty()){
            System.out.println("You have uncommitted changes.");
            System.exit(0);
        }
        if(!commitTree.branch_exist(branchname)){
            System.out.println("A branch with that name does not exist.");
            System.exit(0);
        }
        if(commitTree.get_cur_branch().equals(branchname)){
            System.out.println("Cannot merge a branch with itself.");
            System.exit(0);
        }
        List<String> list=plainFilenamesIn(CWD);
        for(String filename : list) {
            //check whether there is blob
            Blob blob_for_check = new Blob(CWD, filename);
            String val = blob_for_check.get_Hashval();
            File file_for_check = join(BLOB_DIR, val);
            //if not commit, then error
            if (!file_for_check.exists()) {
                System.out.println("There is an untracked file in the way; delete it, or add and commit it first.");
                System.exit(0);
            }
        }
        Node branchnode = commitTree.get_branch_node(branchname);
        Node currentnode = commitTree.get_cur_branch_node();
        boolean hasconflict=false;


        /** Find the split point */
        Node split_point = split_point(currentnode, branchnode);

        //System.out.println("Current Node: "+currentnode.get_node_data());
        //System.out.println("Branch Node: "+branchnode.get_node_data());
        //System.out.println("Split Node: "+split_point.get_node_data());

        //if split point is the given branch
        if (split_point.equals(branchnode)) {
            System.out.println("Given branch is an ancestor of the current branch.");
            return;
            //else if the split point the current head
        } else if (split_point.equals(currentnode)) {
            String commitID = branchnode.get_node_data();
            Commit commit = readCommit(join(COMMIT_DIR, commitID));
            checkout_commit(commit);
            System.out.println("Current branch fast-forwarded.");
            return;
        }

        Commit branchcommit = readCommit(join(COMMIT_DIR, branchnode.get_node_data()));
        Commit currentcommit = readCommit(join(COMMIT_DIR, currentnode.get_node_data()));
        Commit splitnodecommit = readCommit(join(COMMIT_DIR, split_point.get_node_data()));

        FileTree branchfiletree = branchcommit.get_file_tree();
        FileTree currentfiletree = currentcommit.get_file_tree();
        FileTree splitpointfiletree = splitnodecommit.get_file_tree();

//        branchcommit.print_filetree();

        for (String key : branchfiletree.keySet()) {
            String hashval_branch;
            String hashval_current;
            String hashval_splitpoint;
            hashval_branch = branchfiletree.get(key);
            hashval_current = currentfiletree.get(key);
            hashval_splitpoint = splitpointfiletree.get(key);

//           System.out.println("File: "+key);
//           System.out.println("Branch: "+hashval_branch);
//           System.out.println("Current branch: "+hashval_current);
//           System.out.println("Splitpoint: "+hashval_splitpoint);

            if (hashval_branch != null && hashval_splitpoint == null && hashval_current == null) {
//                System.out.println("File: "+key);
                Blob blob = readBlob(join(BLOB_DIR, hashval_branch));
                blob.writeContents_Filename(CWD);
                addCommand(key);
                break;
            }
            else if (hashval_splitpoint != null && hashval_branch != null && hashval_current == null) {
                if (hashval_splitpoint.equals(hashval_branch)) {
                    //Situation 7
                    continue;
                }
            }
            else if (hashval_current == null && hashval_branch != null && hashval_splitpoint != null) {
                //one are changed and the other file is deleted
                if (!hashval_branch.equals(hashval_splitpoint)) {
                    //Situation 8
                    conflict(currentfiletree,branchfiletree,key);
                    hasconflict=true;
                }
            }
        }

        for (String key : currentfiletree.keySet()) {
            String hashval_branch;
            String hashval_current;
            String hashval_splitpoint;
            hashval_branch = branchfiletree.get(key);
            hashval_current = currentfiletree.get(key);
            hashval_splitpoint = splitpointfiletree.get(key);

            //System.out.println("File: "+key);

            if (hashval_current != null && hashval_branch != null &&  hashval_splitpoint != null) {
                if (!hashval_branch.equals(hashval_splitpoint) && hashval_current.equals(hashval_splitpoint)) {
                    //Situation 1
                    Blob blob = readBlob(join(BLOB_DIR, hashval_branch));
                    blob.writeContents_Filename(CWD);
                    addCommand(key);
                    continue;
                } else if (!hashval_current.equals(hashval_splitpoint) && hashval_branch.equals(hashval_splitpoint)) {
                    //Situation 2
                    continue;
                } else if (hashval_current.equals(hashval_branch) && !hashval_current.equals(hashval_splitpoint)) {
                    //Situation 3.1
                    continue;
                    //keep
                } else if (!hashval_branch.equals(hashval_current) &&
                        !hashval_branch.equals(hashval_splitpoint) &&
                        !hashval_current.equals(hashval_splitpoint)) {
                    //both are changed and different from other
                    conflict(currentfiletree, branchfiletree, key);
                    hasconflict = true;
                }

            } else if (hashval_current != null  && hashval_branch == null && hashval_splitpoint == null) {
                //Situation 4
                break;
            } else if ( hashval_current != null && hashval_branch == null && hashval_splitpoint != null) {
                if (hashval_splitpoint.equals(hashval_current)) {
                    //Situation 6
                    stageindex.put(key, "removal");
                    File file = join(CWD, key);
                    restrictedDelete(file);
                }  else if (!hashval_current.equals(hashval_splitpoint)) {
                    //one are changed and the other file is deleted
                    conflict(currentfiletree, branchfiletree, key);
                    hasconflict = true;
                }
            } else if (hashval_current != null && hashval_branch != null && hashval_splitpoint == null) {
                // absent at the split point and has different contents in the given and current branches
                if (!hashval_branch.equals(hashval_current)) {
                    conflict(currentfiletree, branchfiletree, key);
                    hasconflict = true;
                }
            }

        }

        for(String filename : list){
            if(!branchfiletree.containsKey(filename) && !currentfiletree.containsKey(filename)){
                //Situation 3.2
                continue;
            }
        }

        for (String key : splitpointfiletree.keySet()){
            String hashval_branch;
            String hashval_current;
            String hashval_splitpoint;
            hashval_branch = branchfiletree.get(key);
            hashval_current = currentfiletree.get(key);
            hashval_splitpoint = splitpointfiletree.get(key);

            if(!(hashval_splitpoint==null) && hashval_current==null && hashval_branch==null){
                //Situation 3.2
                continue;
            }
        }

        //Final step
        commit_merge("Merged "+branchname+" into "+commitTree.get_cur_branch()+".",branchnode);

        //System.out.println("Merge "+branchname+"into "+commitTree.get_cur_branch());
        if(hasconflict){
            System.out.println("Encountered a merge conflict.");
        }


    }

    private static void conflict (FileTree currentfiletree, FileTree branchfiletree, String filename){
        Blob currentblob;
        Blob branchblob;
        //System.out.println("Conflicting file: "+filename);
        if(currentfiletree.containsKey(filename)){
            currentblob= readBlob(join(BLOB_DIR, currentfiletree.get(filename)));
        } else {
            currentblob=null;
        }

        if(branchfiletree.containsKey(filename)){
            branchblob= readBlob(join(BLOB_DIR, branchfiletree.get(filename)));
        } else {
            branchblob=null;
        }
        String start="<<<<<<< HEAD\n";
        String mid  ="=======\n";
        String end  =">>>>>>>\n";
        byte[] total = null;
        if(currentblob==null && branchblob != null){
            total=concat(start.getBytes(),mid.getBytes(),branchblob.getcontents(),end.getBytes());
        }else if(currentblob != null && branchblob == null){
            total=concat(start.getBytes(),currentblob.getcontents(),mid.getBytes(),end.getBytes());
        }else if(currentblob != null && branchblob != null){
            total=concat(start.getBytes(),currentblob.getcontents(),mid.getBytes(),branchblob.getcontents(),end.getBytes());
        }
        assert total != null;
        Blob new_blob=new Blob(filename,total);
        new_blob.writeContents_Filename(CWD);
        //System.out.println(new String(new_blob.getcontents()));
        //System.out.println(new_blob.get_Hashval());

        addCommand(filename);
    }

    private static byte[] concat(byte[]... arrays) {
        int totalLength = 0;

        for (byte[] array : arrays) {
            totalLength += array.length;
        }
        byte[] result = new byte[totalLength];
        int position = 0;

        for (byte[] array : arrays) {
            System.arraycopy(array, 0, result, position, array.length);
            position += array.length;
        }
        return result;
    }
}


