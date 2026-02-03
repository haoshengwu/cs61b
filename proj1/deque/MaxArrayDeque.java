package deque;

import java.util.Comparator;

public class MaxArrayDeque<Item> extends ArrayDeque<Item> {

    private Comparator<Item> comparator;

    public MaxArrayDeque(Comparator<Item> c) {
        super();
        comparator = c;
    }

    public Item max(){
        if(isEmpty()){
            return null;
        } else {
            Item a = get(0);
            for(int i = 1; i < this.size(); i++){
                if(comparator.compare(a,get(i)) < 0){
                    a = get(i);
                }
            }
            return a;
        }
    }

    public Item max(Comparator<Item> c) {
        if(isEmpty()){
            return null;
        } else  {
            Item a = get(0);
            for(int i = 1; i < this.size(); i++){
                if(c.compare(a, get(i))>0){
                    a = get(i);
                }
            }
            return a;
        }
    }

}
