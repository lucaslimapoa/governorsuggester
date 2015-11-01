package com.tcc.lucas.governorsuggestor;

import java.util.HashMap;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Application
{

    private String mName;
    private double mRAMPercent;
    private double mCPUPercent;
    private double mBatteryPercent;

    public Application(String name)
    {
        mName = name;
    }

    public String getName()
    {
        return mName;
    }

    public void setName(String mName)
    {
        this.mName = mName;
    }

    public double getRAMPercent()
    {
        return mRAMPercent;
    }

    public void setRAMPercent(double mRAMPercent)
    {
        this.mRAMPercent = mRAMPercent;
    }

    public double getCPUPercent()
    {
        return mCPUPercent;
    }

    public void setCPUPercent(double mCPUPercent)
    {
        this.mCPUPercent = mCPUPercent;
    }

    public double getBatteryPercent()
    {
        return mBatteryPercent;
    }

    public void setBatteryPercent(double mBatteryPercent)
    {
        this.mBatteryPercent = mBatteryPercent;
    }
}