package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        AList<Integer> N=new AList<>();
        AList<Double> times=new AList<>();
        AList<Integer> opCounts=new AList<>();

        int size=1000;
        int fac=8;
        int operation=10000;
        for(int i=0;i<fac;i++) {
            SLList<Double> list=new SLList<>();
            for(int j=0;j<size;j++) {
                list.addLast(1.0);
            }
            Stopwatch sw = new Stopwatch();
            for(int k=0;k<operation;k++) {
                list.getLast();
            }
            double timeInSeconds = sw.elapsedTime();

            N.addLast(size);
            opCounts.addLast(operation);
            times.addLast(timeInSeconds);
            size=size*2;
        }
        printTimingTable(N, times, opCounts);

    }

}
