package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/26/2015.
 */
public class ProcessStats
{
    private String mPackageName;
    private String mUid;
    private double mTotalTime;
    private MemoryStats mMemoryStats;
    private double mBatteryUsage;

    public ProcessStats()
    {

    }

    public String getPackageName()
    {
        return mPackageName;
    }

    public void setPackageName(String mName)
    {
        this.mPackageName = mName;
    }

    public double getTotalTime()
    {
        return mTotalTime;
    }

    public void setTotalTime(double mTotalTime)
    {
        this.mTotalTime = mTotalTime;
    }

    public MemoryStats getMemoryStats()
    {
        return mMemoryStats;
    }

    public void setMemoryStats(MemoryStats mMemoryStats)
    {
        this.mMemoryStats = mMemoryStats;
    }

    public String getUid()
    {
        return mUid;
    }

    public void setUid(String mUuid)
    {
        this.mUid = mUuid;
    }

    public double getBatteryUsage()
    {
        return mBatteryUsage;
    }

    public void setBatteryUsage(double mBatteryUsage)
    {
        this.mBatteryUsage = mBatteryUsage;
    }
}

