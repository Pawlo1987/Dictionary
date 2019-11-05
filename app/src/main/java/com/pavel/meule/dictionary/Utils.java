package com.pavel.meule.dictionary;

import java.util.Random;
import java.util.concurrent.TimeUnit;

public class Utils {
    static Random rand = new Random();

    public static int getRandom(int lo, int hi) {
        return (lo + rand.nextInt(hi - lo));
    } // getRandom

    public static double getRandom(double lo, double hi) {
        double value = lo + (hi - lo) * rand.nextDouble();
        return value;
    } // getRandom

    public static void sleep(int time){
        try {
            TimeUnit.MILLISECONDS.sleep(time);//ждем
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


