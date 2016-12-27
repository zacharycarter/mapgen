package com.carterza.universe.generator;

public class Util
{

    // Cell length in meters
    static final float l = 1;

    // Cell area in meters^2
    static final float A = l*l;

    public static double fastpow(final double a, final double b)
    {
        final long tmp = Double.doubleToLongBits(a);
        final long tmp2 = (long)(b * (tmp - 4606921280493453312L)) + 4606921280493453312L;
        return Double.longBitsToDouble(tmp2);
    }

    public static double random(float low, float high)
    {
        return (high - low) * Math.random() + low;
    }

    // http://en.wikipedia.org/wiki/Poisson_distribution#Generating_Poisson-distributed_random_variables
    public static double poissonRandom(float expectedValue)
    {
        float l = (float)fastpow(Math.E, -expectedValue);
        int k = 0;
        float p = 1;
        do
        {
            k++;
            p*=Math.random();
        }
        while (p > l);
        return k - 1;
    }
    /**
     * Calculate pressure in Pa given height in m.
     * @see http://en.wikipedia.org/wiki/Atmospheric_pressure
     */
    public static float pressurePaByHeightM(float h)
    {
        return  101.325f*(float)fastpow(1.0-0.0065*h/288.15, (8.80665*0.0289)/(8.3144/0.0065));
    }

    /**
     * Atmospheric temperature by height in meters.
     * @see http://www.kansasflyer.org/index.asp?nav=Avi&sec=Alti&tab=Theory&pg=2
     * @param h Height in meters above sea level.
     * @param lattitude [0,1), 0 is most northern, 1 is most southern.
     * @return The atmospheric temperature at this height in Kelvin.
     */
    public static float temperatureByHeightAndLattitudeAndTime(float h, float v, float minutesElapsedInDay)
    {
        return temperatureByHeightAndLattitudeAndTime(h, v, 0, minutesElapsedInDay);
    }

    public static float temperatureByHeightAndLattitudeAndTime(float h, float v, float humidity, float minutesElapsedInDay)
    {
        // temperature in C.
        float Tc = 15 + (-6.5f * (h-3200)/1000);

        float lattitudeAdjustmentC = (float)(80.0f*Math.abs(v-0.5)-15);

        // Under clouds? Let the clouds block the light, cooler temperature.
        float humidityAdjustment = 0;//humidity> 100 ? 20*humidity/100:0;

        // Adjust for day/night temperatures +/- 7C
        float diurnalAdjustment = 0;
        float temperatureVariance = -7;
        diurnalAdjustment = (float)(temperatureVariance * Math.sin(minutesElapsedInDay*2*Math.PI/720));

        // Temperature in K.
        // Don't return a negative number, absolute zero and all.
        return Math.max(0.01f, (Tc+273-lattitudeAdjustmentC-humidityAdjustment)+diurnalAdjustment);
    }

    /**
     * Calculate the partial pressure of water vapor required to saturate air.
     * Shamelessly copied from
     * http://www.nco.ncep.noaa.gov/pmb/codes/nwprod/rap.v1.0.6/sorc/rap_gsi.fd/gsdcloud/adaslib.f90
     * @param Ps the air pressure in Pascals
     * @param T the air temperature in Kelvin
     * @return The partial pressure of water vapor required to saturate the air in Pascals
     */
    public static float waterVaporSaturationThreshold(float Ps, float T)
    {
        final float satfwa = 1.0007f;
        final float satfwb = 3.46E-8f;
        final float satewa = 611.21f;
        final float satewb = 17.502f;
        final float satewc = 32.18f;

        final float f = satfwa + satfwb * Ps;
        final float fesl = (float)(f * satewa * fastpow((float)Math.E, satewb*(T-273.15)/(T-satewc)));
        return fesl;
    }

    /**
     * Calculate the mass of water vapor in grams that occupies a given volume
     * with a given pressure and temperature.
     * @param V The volume occupied by the water vapor in m^3.
     * @param P The partial pressure exerted by the vapor in Pascals.
     * @param T The temperature of the vapor in Kelvin.
     * @return Mass of water in grams.
     */
    public static float waterVaporPartialPressureToMass(float V, float P, float T)
    {
        // R is the ideal gas constant and has the value 8.314 J·K^−1·mol^−1.
        float R = 8.314f;

        //PV=nRT
        // or PV/RT = n
        // n is in moles
        float n = P*V/(R*T);

        // Moar mass of water 18.01528(33) g/mol
        float mm = 18.01528f;

        // moles * grams/mole = grams.
        float mass = n * mm;

        // return mass in grams
        return (mass);
    }

    /**
     * Convert grams of water into height of water
     * @param g Amount of water in grams
     * @param A The area the water takes up.
     * @return The height the water would take up in meters
     */
    public static float waterMassToWaterHeight(float g, float A)
    {
        return (g/(A*100*100*100));
    }


    /**
     * Convert height of water into mass of water
     * @param h The height of the water in meters
     * @param A The area the water takes up.
     * @return The mass of the water in grams.
     */
    public static float waterHeightToWaterMass(float h, float A)
    {
        return (h*(A*100*100*100));
    }
}
