package gh2;

import edu.princeton.cs.algs4.StdAudio;
import edu.princeton.cs.algs4.StdDraw;

public class GuitarHero {
    //public static final double CONCERT_A = 440.0;
    //public static final double CONCERT_C = CONCERT_A * Math.pow(2, 3.0 / 12.0);

    public static String keyboard = "q2we4r5ty7u8i9op-[=zxdcfvgbnjmk,.;/' ";

    public static void main(String[] args) {
        /* create two guitar strings, for concert A and C */
        GuitarString[] stringarray=new GuitarString[37];
        for(int i=0;i<stringarray.length;i++){
            stringarray[i]= new GuitarString( 440* Math.pow(2, i-24.0 / 12.0));
        }
        while (true) {
            /* check if the user has typed a key; if so, process it */
            int idx=0;
            if (StdDraw.hasNextKeyTyped()) {
                char key = StdDraw.nextKeyTyped();
                idx=keyboard.indexOf(key);
                if (idx>=0) {
                    stringarray[idx].pluck();
                }
                else {
                    System.out.println("Unknown Key");
                }
            }

            /* compute the superposition of samples */
            double sample = stringarray[idx].sample();

            /* play the sample on standard audio */
            StdAudio.play(sample);

            /* advance the simulation of each guitar string by one step */
            stringarray[idx].tic();
        }
    }
}
