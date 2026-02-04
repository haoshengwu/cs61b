package deque;

import org.junit.Test;

import java.util.Comparator;
import java.util.Objects;

import static org.junit.Assert.assertEquals;

public class MaxArrayDequeTest {

    Comparator<Integer> valcomp = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            if(Objects.equals(o1, o2)) {
                return 0;
            }
            else if(o1 < o2){
                return -1;
            }
            else {
                return 1;
            }
        }
    };

    @Test
    public void testMaxArrayDeque() {
        MaxArrayDeque<Integer> array = new MaxArrayDeque<>(valcomp);
        for(int i = 0; i < 10; i++) {
            array.addLast(i);
        }
        System.out.println("The max value is " + array.max());
    }

}
