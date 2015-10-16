package com.tcc.lucas.governorsuggestor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Lucas on 10/16/2015.
 */
public class CPUInformation
{
    private final String LOG_TAG = getClass().getSimpleName();

    private String mCurrentCPUFreq;
    private String mMaxCPUFreq;
    private String mMinCPUFreq;

    public CPUInformation()
    {
        mMaxCPUFreq = getMaxCPUFrequency();
        mMinCPUFreq = getMinCPUFrequency();
    }

    private void updateCurrentFrequency()
    {
        try
        {
            File statusFile = new File(Definitions.FOLDER_SYSTEM_CPU + "/" + Definitions.FILE_SYSTEM_CURRENT_CPU_FREQ);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

            int frequency = Integer.parseInt(bufferedReader.readLine()) / 1000;
            mCurrentCPUFreq = String.valueOf(frequency) + " MHz";
        }

        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, "scaling_cur_freq file not found.");
            e.printStackTrace();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "cannot open scaling_cur_freq file.");
            e.printStackTrace();
        }
    }

    private String getMaxCPUFrequency()
    {
        String maxFrequency = new String();

        try
        {
            File statusFile = new File(Definitions.FOLDER_SYSTEM_CPU + "/" + Definitions.FILE_SYSTEM_MAX_CPU_FREQ);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

            maxFrequency = bufferedReader.readLine();
        }

        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, "cpuinfo_max_freq file not found.");
            e.printStackTrace();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "cannot open cpuinfo_max_freq file.");
            e.printStackTrace();
        }

        return maxFrequency;
    }

    private String getMinCPUFrequency()
    {
        String minFrequency = new String();

        try
        {
            File statusFile = new File(Definitions.FOLDER_SYSTEM_CPU + "/" + Definitions.FILE_SYSTEM_MIN_CPU_FREQ);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

            minFrequency = bufferedReader.readLine();
        }

        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, "cpuinfo_min_freq file not found.");
            e.printStackTrace();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "cannot open cpuinfo_min_freq file.");
            e.printStackTrace();
        }

        return minFrequency;
    }

    public String getCurrentCPUFreq()
    {
        updateCurrentFrequency();
        return mCurrentCPUFreq;
    }

    public String getMaxCPUFreq()
    {
        return mMaxCPUFreq;
    }

    public String getMinCPUFreq()
    {
        return mMinCPUFreq;
    }
}
