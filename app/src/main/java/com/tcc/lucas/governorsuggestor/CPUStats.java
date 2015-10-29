package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/27/2015.
 */
public class CPUStats
{
    private double mCPUUser;
    private double mCPUKernel;
    private double mCPUForeground;

    public CPUStats() { }

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
