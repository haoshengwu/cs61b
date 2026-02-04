package deque;

import java.util.Iterator;

public interface Deque<T> {
//    public boolean isEmpty();
    default public boolean isEmpty(){
    if(this.size()==0){
        return true;
    }
    return false;
    }

    public int size();
    public void addFirst(T item);
    public void addLast(T item);
    public T removeFirst();
    public T removeLast();
    public void printDeque();
    public T get(int index);
    public Iterator<T> iterator();
    public boolean equals(Object o);
}
