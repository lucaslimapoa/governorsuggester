package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/26/2015.
 */
public class ProcessStats
{
    private String mPackageName;
    private MemoryStats mMemoryStats;

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

    public MemoryStats getMemoryStats()
    {
        return mMemoryStats;
    }

    public void setMemoryStats(MemoryStats mMemoryStats)
    {
        this.mMemoryStats = mMemoryStats;
    }
}

