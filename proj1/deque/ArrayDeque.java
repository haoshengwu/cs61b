package deque;

import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T> {
    private int size=0;
    private int length;
    private T[] items;
    private int nextLast;
    private int nextFirst;

    public ArrayDeque() {
        size=0;
        length=8;
        items=(T []) new Object[length];
        nextLast=4;
        nextFirst=3;
    }
//    @Override
//    public boolean isEmpty() {
//        return size == 0;
//    }

    @Override
    public int size(){
        return size;
    }

    private void resize(int cap) {
        T[] newItems = (T []) new Object[cap];

        for(int i=0;i<=size-1;i++){
            newItems[i]=this.get(i);
        }
        this.length=cap;
        this.nextFirst=cap-1;
        this.nextLast=size;
        this.items=newItems;

    }

    @Override
    public void addFirst(T item) {
        if(size==length){
            resize(length*2);
        }
        items[nextFirst]=item;
        nextFirst--;
        if(nextFirst<0){
            nextFirst=length-1;
        }
        size++;
    }

    @Override
    public void addLast(T item) {
        if(size==length){
            resize(length*2);
        }
        items[nextLast]=item;
        nextLast++;
        if(nextLast>length-1){
            nextLast=0;
        }
        size++;
    }

    @Override
    public T removeFirst() {
        if(isEmpty()){
            System.out.println("The Array is empty");
            return null;
        }

        if (size < 0.25 * length && length >= 16) {
            int newLength = Math.max((int) Math.ceil(length * 0.25), 8);
            resize(newLength);
        }

        nextFirst++;
        if(nextFirst>length-1){
            nextFirst=0;
        }
        T firstitem=items[nextFirst];
        size--;
        items[nextFirst]=null;


        return firstitem;



    }

    @Override
    public T removeLast(){
        if(isEmpty()){
            System.out.println("The Array is empty");
            return null;
        }

        if (size < 0.25 * length && length >= 16) {
            int newLength = Math.max((int) Math.ceil(length * 0.25), 8);
            resize(newLength);
        }

        nextLast--;
        if(nextLast<0){
            nextLast=length-1;
        }
        T lastitem=items[nextLast];
        items[nextLast]=null;
        size--;

        return lastitem;
    }

    @Override
    public T get(int index){
        if(index > size-1){
            return null;
        }
        int tmp;
        tmp=nextFirst+1+index;
        if(tmp>length-1){
            tmp=tmp-length;
        }
        return items[tmp];
    }



    @Override
    public void printDeque(){
        for(int i=0;i<size;i++){
            System.out.print(get(i)+" ");
        }
        System.out.print("/n");

    }


   public class ArrayDequeIterator implements Iterator<T> {
        int wizpos;
        public ArrayDequeIterator(){
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
        return new ArrayDequeIterator();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Deque)) {
            return false;
        }

        Deque other = (Deque) o;

        if (this.size != other.size()) {
            return false;
        }

        for (int i = 0; i < size; i++) {
            if (!this.get(i).equals(other.get(i))) {
                return false;
            }
        }

        return true;
    }

    public static void main(String[] args){
        ArrayDeque<Integer> array=new ArrayDeque<>();
        for(int i=0;i<32;i++){
            array.addLast(i);
        }
        array.printDeque();

        Iterator<Integer> iterator=array.iterator();
        while(iterator.hasNext()) {
            System.out.println(iterator.next());
        }

    }



}
