package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        AList<Integer> N=new AList<>();
        AList<Double> times=new AList<>();
        AList<Integer> opCounts=new AList<>();

        AList<Double> list=new AList<>();
        int base=1000;

        int fac =8;
        double tot_time=0.0;
        for(int i=0;i<=fac;i++){
            int end=(int)Math.pow(2,i)*base;
            Stopwatch sw = new Stopwatch();
            for(int j=i;j<=end;j++){
                list.addLast(1.0);
            }
            double timeInSeconds = sw.elapsedTime();
            N.addLast(end);
            opCounts.addLast(end);
            tot_time+=timeInSeconds;
            times.addLast(tot_time);

        }
        printTimingTable(N, times, opCounts);
    }
}
