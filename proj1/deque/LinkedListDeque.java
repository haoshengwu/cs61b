package deque;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

public class LinkedListDeque<T> implements  Deque<T> {
    private static final Logger log = LoggerFactory.getLogger(LinkedListDeque.class);
    private Node<T> sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel=new Node<>(null,null, null);
        sentinel.next=sentinel;
        sentinel.prev=sentinel;
        size=0;
    }

//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void addFirst(T item) {
        Node<T> tmp=sentinel.next;
        Node<T> newnode=new Node<>(item, sentinel,tmp);
        tmp.prev=newnode;
        sentinel.next=newnode;
        size++;
    }

    @Override
    public void addLast(T item) {
        Node<T> tmp=sentinel.prev;
        Node<T> newnode=new Node<>(item, tmp, sentinel);
        tmp.next=newnode;
        sentinel.prev=newnode;
        size++;
    }

    @Override
    public T removeFirst() {
        if(this.isEmpty()){
            System.out.println("The deque is empty");
            return null;
        }
        T firstitem=sentinel.next.item;
        Node<T> tmp=sentinel.next.next;
        tmp.prev=sentinel;
        sentinel.next=tmp;
        size--;
        return firstitem;
    }

    @Override
    public T removeLast() {
        if(this.isEmpty()){
            System.out.println("The deque is empty");
            return null;
        }
        T lastitem=sentinel.prev.item;

        Node<T> tmp=sentinel.prev.prev;
        tmp.next=sentinel;
        sentinel.prev=tmp;
        size--;
        return lastitem;
    }

    private static class Node<T> {
        T item;
        Node<T> prev;
        Node<T> next;
        Node(T item, Node<T> prev, Node<T> next) {
            this.item = item;
            this.prev = prev;
            this.next = next;
        }
    }

    @Override
    public void printDeque(){
        Node<T> tmp=sentinel.next;
        while(tmp!=sentinel){
            System.out.print(tmp.item+" ");
            tmp = tmp.next;
        }
        System.out.print("/n");
    }

    @Override
    public T get(int index){
        if(index > this.size-1){
            return null;
        }
        Node<T> tmp=sentinel;
        for(int i=0;i<=index;i++){
            tmp = tmp.next;
        }
        return tmp.item;
    }

    private T getRecursiveHelp(int index, Node<T> node){
        if(index==0){
            return node.item;
        }else{
            return getRecursiveHelp(index-1,node.next);
        }
    }

    public T getRecursive(int index){
        if(index>this.size-1){
            return null;
        }
        return getRecursiveHelp(index,sentinel.next);
    }


    public class LinkedListIterator implements Iterator<T> {
        int wizpos;
        public LinkedListIterator(){
            wizpos=0;
        }
        @Override
        public T next() {
            if(!hasNext()){
                System.out.println("The Array is empty");
            }else{
                T tmp=get(wizpos);
                wizpos++;
                return tmp;
            }
            return null;
        }
        @Override
        public boolean hasNext() {
            return wizpos < size;
        }
    }


    public Iterator<T> iterator(){
        return new LinkedListIterator();
    }

    @Override
    public boolean equals(Object o){
        if (this == o) { return true; }
        if (o instanceof Deque) {
            if (this.size != ((Deque)o).size()) { return false; }
            for(int i= 0;i<size;i++){
                if (this.get(i).equals(((Deque)o).get(i))) { return true; }
            }
            return true;
        }
        return false;
    }
    public static void main(String[] args) {
        LinkedListDeque<Integer> L1=new LinkedListDeque<>();
        System.out.println("Successfully create the Deque.");

    }
}
