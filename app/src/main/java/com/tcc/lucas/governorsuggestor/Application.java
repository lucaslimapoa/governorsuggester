package com.tcc.lucas.governorsuggestor;

import java.util.HashMap;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Application
{

    private String mName;
    private Definitions.ApplicationRank mRank;
    private float mBatteryUsed;
    private long mRunTime;
    private long mVirtualRAM;
    private long mPhysicalRAM;
    private float mCpuUsed;
    private long mBytesSent;
    private long mBytesReceived;
    private HashMap<Definitions.Governor, Double> mGovernorScores;

    public Application()
    {
    }

    public Application(String mName, Definitions.ApplicationRank mRank, float mBatteryUsed, long mUsage)
    {
        this.mName = mName;
        this.mRank = mRank;
        this.mBatteryUsed = mBatteryUsed;
        this.mRunTime = mUsage;
    }

    public String getName()
    {
        return mName;
    }

    public Definitions.ApplicationRank getRank()
    {
        return mRank;
    }

    public float getBatteryUsed()
    {
        return mBatteryUsed;
    }

    public float getRunTime()
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

    public void setRank(Definitions.ApplicationRank mRank)
    {
        this.mRank = mRank;
    }

    public void setBatteryUsed(float mBatteryUsed)
    {
        this.mBatteryUsed = mBatteryUsed;
    }

    public void setRunTime(long mUsage)
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

    public HashMap<Definitions.Governor, Double> getGovernorScores()
    {
        return mGovernorScores;
    }

    public void setGovernorScores(HashMap<Definitions.Governor, Double> mGovernorScores)
    {
        this.mGovernorScores = mGovernorScores;
    }
}