package com.hna.unaati.unnati_soil;

/**
 * Created by defcon on 22/03/18.
 */

public class prediction {
    public double organicCarbon, totalNitorgen,totalPhosphorous, sand, pH, clay ;

    public prediction(double sand, double clay, double pH, double organicCarbon  ){
        this.clay = clay ;
        this.pH = pH;
        this.organicCarbon = organicCarbon ;
        this.sand = sand ;
        findTotalNitrogen();
    }

    private void findTotalNitrogen(){
        totalNitorgen =  0.026 + 0.067*organicCarbon ;
    }

    public double getTotalNitorgen(){
        return this.totalNitorgen;
    }

    private void findTotalPhosphorous(){ totalPhosphorous = 0.7927 * Math.exp(4.9922*organicCarbon);}
    public double getTotalPhosphorous(){
        return this.totalPhosphorous;
    }
}
