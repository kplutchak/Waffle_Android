package com.pipit.waffle;

import java.util.Random;

/**
 * Created by Eric on 4/9/2015.
 *
 * Holds functions for static calculations used in AnsweringFragment
 */
public class AnsweringFragmentHelpers {

    public final static long ANIM_STARTING_OFFSET = 200;
    private static double TENSION = 800;
    private static double DAMPER = 20; //friction

    /**
     *  magical custom formula for creating appropriate slide speeds varying by travel distance
     * @param dist
     * @return dur - used to set animation for cardView
     */
    public static long calculateSlideSpeed(float dist){
        long dur = (long) ((dist / 10) * (dist / 10)) / 2;
        if (dur > 500)
            dur = 500;
        if (dur < 300)
            dur = 300;
        return dur;
    }

    public static int randInt(int min, int max) {

        // NOTE: Usually this should be a field rather than a method
        // variable so that it is not re-seeded every call.
        Random rand = new Random();

        // nextInt is normally exclusive of the top value,
        // so add 1 to make it inclusive
        int randomNum = rand.nextInt((max - min) + 1) + min;

        return randomNum;
    }

}
