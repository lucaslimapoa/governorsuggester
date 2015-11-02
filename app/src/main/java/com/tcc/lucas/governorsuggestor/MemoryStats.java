package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/26/2015.
 */
public class MemoryStats
{
    private String mPackageName;

    private double mMaxPss;
    private double mAvgPss;
    private double mMinPss;

    private double mMaxUss;
    private double mAvgUss;
    private double mMinUss;

    public MemoryStats()
    {

    }

    public double getMaxPss()
    {
        return mMaxPss;
    }

    public void setMaxPss(double mMaxPss)
    {
        this.mMaxPss = mMaxPss;
    }

    public double getAvgPss()
    {
        return mAvgPss;
    }

    public void setAvgPss(double mAvgPss)
    {
        this.mAvgPss = mAvgPss;
    }

    public double getMinPss()
    {
        return mMinPss;
    }

    public void setMinPss(double mMinPss)
    {
        this.mMinPss = mMinPss;
    }

    public double getMaxUss()
    {
        return mMaxUss;
    }

    public void setMaxUss(double mMaxUss)
    {
        this.mMaxUss = mMaxUss;
    }

    public double getAvgUss()
    {
        return mAvgUss;
    }

    public void setAvgUss(double mAvgUss)
    {
        this.mAvgUss = mAvgUss;
    }

    public double getMinUss()
    {
        return mMinUss;
    }

    public void setMinUss(double mMinUss)
    {
        this.mMinUss = mMinUss;
    }

    public String getPackageName()
    {
        return mPackageName;
    }

    public void setPackageName(String mPackageName)
    {
        this.mPackageName = mPackageName;
    }
}
