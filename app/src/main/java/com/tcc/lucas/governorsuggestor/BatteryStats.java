package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/29/2015.
 */
public class BatteryStats
{
    private double mBatteryUsed;
    private double mCPUUser;
    private double mCPUKernel;
    private double mCPUForeground;
    private double mCPUPercent;

    public BatteryStats()
    {

    }

    public boolean isValid()
    {
        boolean isValid = false;

        if(mBatteryUsed != 0 || mCPUForeground != 0 || mCPUKernel != 0 || mCPUUser != 0)
            isValid = true;

        return isValid;
    }

    public double getTotalCPUTime()
    {
        return mCPUUser + mCPUKernel + mCPUForeground;
    }

    public void setCPUPercent(double cpuPercent)
    {
        this.mCPUPercent = cpuPercent;
    }

    public double getCPUPercent()
    {
        return mCPUPercent;
    }

    public double getBatteryUsed()
    {
        return mBatteryUsed;
    }

    public void setBatteryUsed(double mBatteryUsed)
    {
        this.mBatteryUsed = mBatteryUsed;
    }

    public double getCPUUser()
    {
        return mCPUUser;
    }

    public void setCPUUser(double mCPUUser)
    {
        this.mCPUUser = mCPUUser;
    }

    public double getCPUKernel()
    {
        return mCPUKernel;
    }

    public void setCPUKernel(double mCPUKernel)
    {
        this.mCPUKernel = mCPUKernel;
    }

    public double getCPUForeground()
    {
        return mCPUForeground;
    }

    public void setCPUForeground(double mCPUForeground)
    {
        this.mCPUForeground = mCPUForeground;
    }
}
