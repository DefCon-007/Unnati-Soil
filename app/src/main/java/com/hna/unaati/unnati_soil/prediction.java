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


    public double getNitrogenFertilizer(double nutInCom){
        return getFertlizerAmount(90,nutInCom);
    }

    public double getPhosphorusFertilizer(double nutInCom){
        return getFertlizerAmount(40,nutInCom);
    }

    public double getPotassiumFertilizer(double nutInCom){
        return getFertlizerAmount(55,nutInCom);
    }

    public int getLimeStoneValueLow(){
        if (sand > 70 && clay < 20)
            return 600;
        else if (sand > 50 && clay < 20)
            return  1100;
        else if (sand > 30 && clay < 30)
            return 1700;
        else if (sand > 0 && clay < 30)
            return 2700;
        else
            return 3350;
    }
    public int getLimeStoneValueHigh(){
        if (sand > 70 && clay < 20)
            return 900;
        else if (sand > 50 && clay < 20)
            return  1550;
        else if (sand > 30 && clay < 30)
            return 2200;
        else if (sand > 0 && clay < 30)
            return 3100;
        else
            return 4200;
    }

    private double getFertlizerAmount(double rec, double nutInCom){
        return ((rec*100)/nutInCom);
    }
}
