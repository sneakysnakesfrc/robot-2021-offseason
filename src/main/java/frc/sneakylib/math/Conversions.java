package frc.sneakylib.math;

import java.lang.Math;

public class Conversions {


      /**
     * Converts radians to degrees. Returns degrees.
     *
     * @param radians radians to convert
     *
     */
    public static double radiansToDegrees(double radians){
        double degrees;
        degrees = radians * (180/Math.PI);
        return degrees;
    }


      /**
     * Converts feets to meters. Returns meters.
     *
     * @param feet feets to convert
     *
     */
    public static double feetToMeters(double feet){
        double meters;
        meters = feet/3.2808;
        return meters;
    }

     /**
     * Converts inches to meters. Returns meters.
     *
     * @param inches inches to convert
     *
     */
    public static double inchesToMeters(double inches){
        double meters;
        meters = inches/39.370;
        return meters;
    }

     /**
     * Converts inches to centimeters. Returns centimeters.
     *
     * @param inches inches to convert
     *
     */
    public static double inchesToCentimeters(double inches){
       return inchesToMeters(inches)*100;
    }
}