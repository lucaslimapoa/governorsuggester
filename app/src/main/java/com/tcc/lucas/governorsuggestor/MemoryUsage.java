package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * Created by Lucas on 10/11/2015.
 */
public class MemoryUsage
{
    private final String LOG_TAG = getClass().getSimpleName();

    private long mMemTotal;
    private long mMemFree;
    private long mBuffers;
    private long mCached;

    public MemoryUsage()
    {
        try
        {
            getStatFileInformation();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void getStatFileInformation() throws IOException
    {
        File statusFile = new File(Definitions.FOLER_PROC + "/" + Definitions.FILE_SYSTEM_MEMORY);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

        String memStats = bufferedReader.readLine();
        String[] data = memStats.split(":");

        String cleanedValue = data[1].substring(0, data[1].length() - 2);
        cleanedValue = cleanedValue.replace(" ", "");
        this.mMemTotal = Long.parseLong(cleanedValue);

        memStats = bufferedReader.readLine();
        data = memStats.split(":");

        cleanedValue = data[1].substring(0, data[1].length() - 2);
        cleanedValue = cleanedValue.replace(" ", "");

        this.mMemFree = Long.parseLong(cleanedValue);

        memStats = bufferedReader.readLine();
        data = memStats.split(":");

        cleanedValue = data[1].substring(0, data[1].length() - 2);
        cleanedValue = cleanedValue.replace(" ", "");

        this.mBuffers = Long.parseLong(cleanedValue);

        memStats = bufferedReader.readLine();
        data = memStats.split(":");

        cleanedValue = data[1].substring(0, data[1].length() - 2);
        cleanedValue = cleanedValue.replace(" ", "");

        this.mCached = Long.parseLong(cleanedValue);
    }
}
