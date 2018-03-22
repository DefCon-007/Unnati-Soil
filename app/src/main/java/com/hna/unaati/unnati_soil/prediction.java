package com.hna.unaati.unnati_soil;

/**
 * Created by defcon on 22/03/18.
 */

public class prediction {
    public double organicCarbon, totalNitorgen, sand, pH, clay ;

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
}
