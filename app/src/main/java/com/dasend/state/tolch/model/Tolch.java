package com.dasend.state.tolch.model;

import java.util.Locale;

public class Tolch {
    private double bad;


    private double neutral;
    private double good;

    public Tolch(double good, double bad, double neutral) {
        this.good = good;
        this.bad = bad;
        this.neutral = neutral;
    }

    public int getFone() {

        if (bad > neutral && bad > good) {
            return -1;
        }

        if (good > neutral && good > bad) {
            return 1;
        }

        return 0;
    }

    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "b:%.3f\nn:%.3f\ng:%.3f", bad, neutral, good);
    }




    public double getBad() {
        return bad;
    }

    public double getNeutral() {
        return neutral;
    }

    public double getGood() {
        return good;
    }


}
