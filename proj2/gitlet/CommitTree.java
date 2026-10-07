package gitlet;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import static gitlet.Utils.*;

public class CommitTree implements Serializable {
    private Node root;
    private Node head;
    private HashMap<String,Node> branches;
    private String cur_branch;

    CommitTree(String rootData) {
        root = new Node(rootData);
        root.children = new LinkedList<Node>();
        head = root;
        branches = new HashMap<String,Node>();
        branches.put("master", root);
        cur_branch = "master";
    }

    public static class Node implements Serializable  {
        private String data;
        private Node parent;
        private List<Node> children;
        public Node(String data) {
            //store the hash value of commit
            this.data = data;
            this.children = new LinkedList<Node>();
            this.parent = null;
        }
        public Node get_node_parent(){
            return this.parent;
        }

        public String get_node_data(){
            return this.data;
        }

        public int get_child_count(){
            return this.children.size();
        }
        public void add_child(Node child){
            this.children.add(child);
        }
    }

    public Node get_branch_node(String key){
        return this.branches.get(key);
    }

    public void set_cur_branch_node_to_commitID(String commitID){
        Node node = get_node_by_commitID(this.root,commitID);
        this.branches.put(this.cur_branch, node);
    }

    public Node get_node_by_commitID(Node node, String commitID) {
        Node temp;
        temp = node;
        if (node.data.equals(commitID)) {
            return node;
        } else {
            for (Node child : node.children) {
                get_node_by_commitID(child, commitID);
            }
        }
        return temp;
    }


    public void set_head_by_commitID(String commitID){
        set_head_by_commitID_help(this.root, commitID);
    }

    private void set_head_by_commitID_help(Node current_node, String commitID){
        if(current_node.data.equals(commitID)){
            this.head = current_node;
        }else {
            for(Node child : current_node.children){
                set_head_by_commitID_help(child,commitID);
            }
        }
    }

    public Node getHead() {
        return head;
    }

    public String getHeaddata() {
        return head.data;
    }
    public HashMap<String,Node> getBranches(){
        return branches;
    }

    public String get_cur_branch(){
        return cur_branch;
    }

    public Node get_cur_branch_node(){
        return this.get_branch_node(this.cur_branch);
    }

    public void update_branch(String branchname){
        this.cur_branch = branchname;
        this.head = branches.get(branchname);
    }

    public boolean branch_exist(String branch){
        return branches.containsKey(branch);
    }

    public void add_branch(String branch){
        this.branches.put(branch, head);
    }

    public void remove_branch(String branch){
        this.branches.remove(branch);
    }

    //add a new node based on head and then move head to the new node
    public void add_node_from_head(String data){
        Node node = new Node(data);
        node.parent = head;
        head.children.add(node);
        head = node;
    }

    public void write_CommitTree(File file){
        writeObject(file, (Serializable) this);
    }

    public static CommitTree read_CommitTree(File file){
        return readObject(file, CommitTree.class);
    }


}
