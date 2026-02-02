package deque;

import org.junit.Test;
import static org.junit.Assert.*;
import edu.princeton.cs.algs4.StdRandom;

public class ArrayDequeTest {
    // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove() {
        ArrayDeque<Integer> correct = new ArrayDeque<>();
        ArrayDeque<Integer> broken = new ArrayDeque<>();

        correct.addLast(5);
        correct.addLast(10);
        correct.addLast(15);

        broken.addLast(5);
        broken.addLast(10);
        broken.addLast(15);

        assertEquals(correct.size(), broken.size());

        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
        assertEquals(correct.removeLast(), broken.removeLast());
    }

    @Test
    public void randomizedTest() {
        ArrayDeque<Integer> L = new ArrayDeque<>();
        ArrayDeque<Integer> M = new ArrayDeque<>();
        int N = 5000;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0) {
                // addLast
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                //System.out.println("L addLast(" + randVal + ")");
                M.addLast(randVal);
                //System.out.println("M addLast(" + randVal + ")");
            } else if (operationNumber ==1) {
                if(L.size()>0 && M.size()>0) {
                    int L_last = L.removeLast();
                    System.out.println("removeLast(" + L_last + ")");
                    int M_last = M.removeLast();
                    System.out.println("removeLast(" + M_last + ")");
                    assertEquals(L_last, M_last);
                } else {
                    continue;
                }
            } else if (operationNumber ==2) {
                if(L.size()>0 && M.size()>0){
                    int Lindex =L.size()-1;
                    System.out.println("Lindex: " + Lindex);
                    int Mindex =M.size()-1;
                    System.out.println("Mindex: " + Mindex);

                    int L_last = L.get(Lindex);
                    System.out.println("get L_last (" + L_last + ")");
                    int M_last = M.get(Mindex);
                    System.out.println("get M_last (" + M_last + ")");
                    assertEquals(L_last, M_last);
                }else{
                    continue;
                }

            } else if (operationNumber == 3) {
                // size
                int L_size = L.size();
                System.out.println("size: " + L_size);
                int M_size = M.size();
                System.out.println("size: " + M_size);
                assertEquals(L_size, M_size);
            }
        }
    }
}
