package com.tcc.lucas.governorsuggestor;

import java.util.HashMap;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Application
{

    private String mName;
    private double mRunTime;
    private long mVirtualRAM;
    private long mPhysicalRAM;
    private double mRAMPercent;
    private float mCpuUsed;
    private double mCPUPercent;
    private long mBytesSent;
    private long mBytesReceived;

    public Application()
    {

    }

    public String getName()
    {
        return mName;
    }

    public double getRunTime()
    {
        return mRunTime;
    }

    public float getVirtualRAM()
    {
        return mVirtualRAM;
    }

    public float getPhysicalRAM()
    {
        return mPhysicalRAM;
    }

    public float getCpuUsed()
    {
        return mCpuUsed;
    }

    public float getBytesSent()
    {
        return mBytesSent;
    }

    public float getBytesReceived()
    {
        return mBytesReceived;
    }

    public void setName(String mName)
    {
        this.mName = mName;
    }

    public void setRunTime(double mUsage)
    {
        this.mRunTime = mUsage;
    }

    public void setVirtualRAM(long mVirtualRAM)
    {
        this.mVirtualRAM = mVirtualRAM;
    }

    public void setPhysicalRAM(long mPhysicalRAM)
    {
        this.mPhysicalRAM = mPhysicalRAM;
    }

    public void setCpuUsed(float mCpuUsed)
    {
        this.mCpuUsed = mCpuUsed;
    }

    public void setBytesSent(long mBytesSent)
    {
        this.mBytesSent = mBytesSent;
    }

    public void setBytesReceived(long mBytesReceived)
    {
        this.mBytesReceived = mBytesReceived;
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
}