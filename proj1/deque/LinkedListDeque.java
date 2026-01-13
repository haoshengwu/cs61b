package deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LinkedListDeque<Item> {
    private static final Logger log = LoggerFactory.getLogger(LinkedListDeque.class);
    private Node<Item> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel=new Node<>(null,null, null);
        sentinel.next=sentinel;
        sentinel.prev=sentinel;
        size=0;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    public int size() {
        return size;
    }

    public void addFirst(Item item) {
        Node<Item> tmp=sentinel.next;
        Node<Item> newnode=new Node<>(item, sentinel,tmp);
        tmp.prev=newnode;
        sentinel.next=newnode;
        size++;
    }

    public void addLast(Item item) {
        Node<Item> tmp=sentinel.prev;
        Node<Item> newnode=new Node<>(item, tmp, sentinel);
        tmp.next=newnode;
        sentinel.prev=newnode;
        size++;
    }

    public Item removeFirst() {
        if(this.isEmpty()){
            System.out.println("The deque is empty");
            return null;
        }
        Item firstitem=sentinel.next.item;
        Node<Item> tmp=sentinel.next.next;
        tmp.prev=sentinel;
        sentinel.next=tmp;
        size--;
        return firstitem;
    }

    public Item removeLast() {
        if(this.isEmpty()){
            System.out.println("The deque is empty");
            return null;
        }
        Item lastitem=sentinel.prev.item;

        Node<Item> tmp=sentinel.prev.prev;
        tmp.next=sentinel;
        sentinel.prev=tmp;
        size--;
        return lastitem;
    }

    private static class Node<Item> {
        Item item;
        Node<Item> prev;
        Node<Item> next;
        Node(Item item, Node<Item> prev, Node<Item> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    public void printDeque(){
        Node<Item> tmp=sentinel.next;
        while(tmp!=sentinel){
            System.out.print(tmp.item+" ");
            tmp=tmp.next;
        }
        System.out.print("/n");
    }

    public Item get(int index){
        if(index < this.size-1){
            return null;
        }
        Node<Item> tmp=sentinel;
        for(int i=0;i<=index;i++){
            tmp=tmp.next;
        }
        return tmp.item;
    }

    private Item getRecursiveHelp(int index, Node<Item> node){
        if(index==0){
            return node.item;
        }else{
            return getRecursiveHelp(index-1,node.next);
        }
    }

    public Item getRecursive(int index){
        if(index<this.size-1){
            return null;
        }
        return getRecursiveHelp(index,sentinel.next);
    }

    public static void main(String[] args) {
        LinkedListDeque<Integer> L1=new LinkedListDeque<>();
        System.out.println("Successfully create the Deque.");

    }
}
