package deque;

import java.util.Iterator;

public interface Deque<Item> {
//    public boolean isEmpty();
    default public boolean isEmpty(){
    if(this.size()==0){
        return true;
    }
    return false;
    }

    public int size();
    public void addFirst(Item item);
    public void addLast(Item item);
    public Item removeFirst();
    public Item removeLast();
    public void printDeque();
    public Item get(int index);
    public Iterator<Item> iterator();
    public boolean equals(Object o);
}
