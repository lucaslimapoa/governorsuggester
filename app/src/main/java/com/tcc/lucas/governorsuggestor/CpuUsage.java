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

        mTotalCpuUsage = mUser + mNice + mSystem + mIdle + mIOWait + mIrq + mSoftIrq;
    }

    private void getStatFileInformation() throws IOException
    {
        File statusFile = new File(Definitions.FOLER_PROC + "/" + Definitions.FILE_PROCESS_STAT);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

        String[] cpuStats = bufferedReader.readLine().split(" ");

        if( cpuStats.length >= 8 )
        {
            mUser    = Long.parseLong(cpuStats[2]);
            mNice    = Long.parseLong(cpuStats[3]);
            mSystem  = Long.parseLong(cpuStats[4]);
            mIdle    = Long.parseLong(cpuStats[5]);
            mIOWait  = Long.parseLong(cpuStats[6]);
            mIrq     = Long.parseLong(cpuStats[7]);
            mSoftIrq = Long.parseLong(cpuStats[8]);
        }
    }
}
