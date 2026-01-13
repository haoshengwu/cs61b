package deque;

public class ArrayDeque<Item> {
    private int size=0;
    private int length;
    private Item[] items;
    private int nextLast;
    private int nextFirst;

    public ArrayDeque() {
        size=0;
        length=8;
        items=(Item []) new Object[length];
        nextLast=4;
        nextFirst=3;
    }

    public boolean isEmpty() {
        return size == 0;
    }

    private void resize(int cap) {
        Item[] newItems = (Item []) new Object[cap];

        for(int i=0;i<length-1;i++){
            newItems[i]=this.get(i);
        }
        this.items=newItems;
        this.nextFirst=cap-1;
        this.nextLast=length;
    }


    public void addFirst(Item item) {
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

    public void addLast(Item item) {
        if(size==length){
            resize(length*2);
        }
        items[nextLast]=item;
        nextLast++;
        if(nextFirst>length-1){
            nextFirst=0;
        }
        size++;
    }

    public Item removeFirst() {
        if(isEmpty()){
            System.out.println("The Array is empty");
            return null;
        }
        if((double) size/length<0.25 && length>=16){
            resize(Math.max(length / 4, 8));
        }

        nextFirst++;
        if(nextFirst>length-1){
            nextFirst=0;
        }
        Item firstitem=items[nextFirst];
        size--;
        return firstitem;

    }

    public Item removeLast(){
        if(isEmpty()){
            System.out.println("The Array is empty");
            return null;
        }
        if((double) size/length<0.25 && length>=16){
            resize(Math.max(length / 4, 8));
        }
        nextLast--;
        if(nextLast<0){
            nextLast=length-1;
        }
        Item lastitem=items[nextLast];
        size--;
        return lastitem;
    }

    public Item get(int index){
        int tmp;
        tmp=nextFirst-1+index;
        if(tmp>length-1){
            tmp=tmp-(length-1);
        }
        return items[tmp];
    }

    public int size(){
        return size;
    }


    public void printDeque(){
        for(int i=0;i<size;i++){
            System.out.print(get(i)+" ");
        }
        System.out.print("/n");

    }






}
