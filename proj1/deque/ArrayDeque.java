package deque;

public class ArrayDeque<Item> implements Deque<Item> {
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
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

//    @Override
//    public int size(){
//        return size;
//    }

    private void resize(int cap) {
        Item[] newItems = (Item []) new Object[cap];

        for(int i=0;i<=size-1;i++){
            newItems[i]=this.get(i);
        }
        this.length=cap;
        this.nextFirst=cap-1;
        this.nextLast=size;
        this.items=newItems;

    }

    @Override
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

    @Override
    public void addLast(Item item) {
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
    public Item removeFirst() {
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
        Item firstitem=items[nextFirst];
        size--;
        items[nextFirst]=null;


        return firstitem;



    }

    @Override
    public Item removeLast(){
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
        Item lastitem=items[nextLast];
        items[nextLast]=null;
        size--;



        return lastitem;
    }

    @Override
    public Item get(int index){
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


    

    public static void main(String[] args){
        ArrayDeque<Integer> array=new ArrayDeque<>();
        for(int i=0;i<32;i++){
            array.addLast(i);
        }


    }



}
