package com.hna.unaati.unnati_soil;

/**
 * Created by defcon on 22/03/18.
 */

public class prediction {

    //For Rice N : P2O5 : K2O = 100:50:60 kg/ha

    public double organicCarbon, totalNitorgen,totalPhosphorous, sand, pH, clay ;
    public double BULK_DENSITY= 1.4; //g/cm^3
    public double DEPTH_RICE = 0.50; //cm
    public double DEPTH_MAIZE; //cm
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

    public double getTotalNitorgenPercentage(){
        return this.totalNitorgen;
    }

    public double getNitrogenVolumeRice(){
        // Unit is t/ha
        return 10000*DEPTH_RICE*BULK_DENSITY*totalNitorgen;
    }

    private void findTotalPhosphorous(){ totalPhosphorous = 0.7927 * Math.exp(4.9922*organicCarbon);}
    public double getTotalPhosphorous(){
        return this.totalPhosphorous;
    }
}
