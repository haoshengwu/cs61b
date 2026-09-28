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
