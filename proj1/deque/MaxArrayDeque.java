package deque;

import java.util.Comparator;

public class MaxArrayDeque<T> extends ArrayDeque<T> {

    private Comparator<T> comparator;

    public MaxArrayDeque(Comparator<T> c) {
        super();
        comparator = c;
    }

    public T max(){
        if(isEmpty()){
            return null;
        } else {
            T a = get(0);
            for(int i = 1; i < this.size(); i++){
                if(comparator.compare(a,get(i)) < 0){
                    a = get(i);
                }
            }
            return a;
        }
    }

    public T max(Comparator<T> c) {
        if(isEmpty()){
            return null;
        } else  {
            T a = get(0);
            for(int i = 1; i < this.size(); i++){
                if(c.compare(a, get(i))>0){
                    a = get(i);
                }
            }
            return a;
        }
    }

}
