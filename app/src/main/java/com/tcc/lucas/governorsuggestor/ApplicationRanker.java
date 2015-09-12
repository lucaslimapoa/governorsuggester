package com.tcc.lucas.governorsuggestor;

import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class ApplicationRanker
{
    private final String LOG_TAG = getClass().getSimpleName();

    private List<ApplicationInfo> mDeviceAppsList;

    public ApplicationRanker(List<ApplicationInfo> mDeviceAppsList)
    {
        this.mDeviceAppsList = mDeviceAppsList;
    }

    public Application rankApplication(UsageStats applicationStats)
    {
        Application rankedApplication = null;

        ApplicationInfo applicationInfo = findApplicationByPackage(applicationStats.getPackageName());

        if(applicationInfo != null)
        {
            rankedApplication = new Application();

            String processId = getProcessFolderByPackage(applicationInfo.packageName);

            if(processId != null)
            {
                ProcessUsage appProcessUsage = new ProcessUsage(processId);

                // Network Information
                rankedApplication.setBytesReceived(TrafficStats.getUidRxBytes(applicationInfo.uid));
                rankedApplication.setBytesReceived(TrafficStats.getUidTxBytes(applicationInfo.uid));
            }
        }

        return rankedApplication;
    }

    public List<Application> rankApplication(List<UsageStats> applicationList)
    {
        List<Application> rankedApplicationsList = new ArrayList<Application>();

        for (UsageStats app : applicationList)
        {
            Application newApplication = rankApplication(app);
            rankedApplicationsList.add(newApplication);
        }

        return rankedApplicationsList;
    }

    private ApplicationInfo findApplicationByPackage(String packageName)
    {
        ApplicationInfo applicationInfo = null;

        for(int i = 0; i < mDeviceAppsList.size(); i++)
        {
            if(mDeviceAppsList.get(i).packageName.equals(packageName))
            {
                applicationInfo = mDeviceAppsList.get(i);
                break;
            }
        }

        return  applicationInfo;
    }

    private String getProcessFolderByPackage(final String packageName)
    {
        String processFolder = null;

        File procFolder = new File(Definitions.FOLER_PROC);

        List<File> procFolderFiles = Arrays.asList(procFolder.listFiles());
        Collections.sort(procFolderFiles);

        for (File iterator : procFolderFiles)
        {
            List<File> subfiles = Arrays.asList(iterator.listFiles());

            for(int i = 0; i < subfiles.size(); i++)
            {
                String fileName = subfiles.get(i).getName();

                if( fileName != null && fileName.equals(Definitions.FILE_PROCESS_CMDLINE) )
                {
                    FileInputStream fileInputStream = null;

                    try
                    {
                        fileInputStream = new FileInputStream(subfiles.get(i));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                        String cmdlineText = bufferedReader.readLine();

                        if(cmdlineText != null && cmdlineText.contains(packageName))
                            return iterator.getAbsolutePath();
                    }

                    catch (FileNotFoundException e)
                    {
                        Log.e(LOG_TAG, "File not found- " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }

                    catch (IOException e)
                    {
                        Log.e(LOG_TAG, "Cannot open file - " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        return processFolder;
    }

    private float getRamUsage()
    {
        float ramUsage = 0;


        return ramUsage;
    }
}
