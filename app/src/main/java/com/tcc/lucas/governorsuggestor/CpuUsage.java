package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Lucas on 10/6/2015.
 */
public class CpuUsage
{
    private final String LOG_TAG = getClass().getSimpleName();

    static final String CPU = "cpu";

    private long mUser;          // Normal processes executing in user mode
    private long mNice;          // Niced processes executing in user mode
    private long mSystem;        // Processes executing in kernel mode
    private long mIdle;          // Twiddling thumbs
    private long mIOWait;        // Waiting for I/O to complete
    private long mIrq;           // Servicing interrupts
    private long mSoftIrq;       // Servicing softirqs
    private long mTotalCpuUsage; // Total cpu usage time in Jiffies

    public CpuUsage()
    {
        try
        {
            getStatFileInformation();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }

        setTotalCpuUsage(getUser() + getNice() + getSystem() + getIdle() + getIOWait() + getIrq() + getSoftIrq());
    }

    private void getStatFileInformation() throws IOException
    {
        File statusFile = new File(Definitions.FOLER_PROC + "/" + Definitions.FILE_PROCESS_STAT);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

        String[] cpuStats = bufferedReader.readLine().split(" ");

        if( cpuStats.length >= 8 )
        {
            setUser(Long.parseLong(cpuStats[2]));
            setNice(Long.parseLong(cpuStats[3]));
            setSystem(Long.parseLong(cpuStats[4]));
            setIdle(Long.parseLong(cpuStats[5]));
            setIOWait(Long.parseLong(cpuStats[6]));
            setIrq(Long.parseLong(cpuStats[7]));
            setSoftIrq(Long.parseLong(cpuStats[8]));
        }
    }

    public long getUser()
    {
        return mUser;
    }

    public void setUser(long mUser)
    {
        this.mUser = mUser;
    }

    public long getNice()
    {
        return mNice;
    }

    public void setNice(long mNice)
    {
        this.mNice = mNice;
    }

    public long getSystem()
    {
        return mSystem;
    }

    public void setSystem(long mSystem)
    {
        this.mSystem = mSystem;
    }

    public long getIdle()
    {
        return mIdle;
    }

    public void setIdle(long mIdle)
    {
        this.mIdle = mIdle;
    }

    public long getIOWait()
    {
        return mIOWait;
    }

    public void setIOWait(long mIOWait)
    {
        this.mIOWait = mIOWait;
    }

    public long getIrq()
    {
        return mIrq;
    }

    public void setIrq(long mIrq)
    {
        this.mIrq = mIrq;
    }

    public long getSoftIrq()
    {
        return mSoftIrq;
    }

    public void setSoftIrq(long mSoftIrq)
    {
        this.mSoftIrq = mSoftIrq;
    }

    public long getTotalCpuUsage()
    {
        return mTotalCpuUsage;
    }

    public void setTotalCpuUsage(long mTotalCpuUsage)
    {
        this.mTotalCpuUsage = mTotalCpuUsage;
    }
}
