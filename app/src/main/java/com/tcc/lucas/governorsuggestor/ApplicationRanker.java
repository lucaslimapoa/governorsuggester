package com.tcc.lucas.governorsuggestor;

import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class ApplicationRanker
{
    private final String LOG_TAG = getClass().getSimpleName();

    private final String COMMAND_PS_NAME = "pidof ";

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
                Process appProcess = new Process(processId);

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
        File[] procFolder = new File(Definitions.FOLER_PROC).listFiles();

        for (File iterator : procFolder)
        {
            if(iterator.isDirectory())
            {
                File[] contents = iterator.listFiles();

                for (File file : contents)
                {
                    if(file.isFile())
                    {
                        if(file.getName().equals(Definitions.FILE_PROCESS_CMDLINE))
                        {
                            try
                            {
                                FileInputStream fileInputStream = new FileInputStream(file);
                                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                                if(bufferedReader.readLine().equals(packageName))
                                    processFolder = file.getAbsolutePath();
                            }

                            catch (FileNotFoundException e)
                            {
                                e.printStackTrace();
                            }

                            catch (IOException e)
                            {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            }
        }







//        try
//        {
//            java.lang.Process command = Runtime.getRuntime().exec(COMMAND_PS_NAME + packageName);
//            BufferedReader outputReader = new BufferedReader(new InputStreamReader(command.getInputStream()));
//
//            String output = outputReader.readLine();
//
//            if(output != null)
//                processId = output;
//        }
//
//        catch (IOException e)
//        {
//            Log.e(LOG_TAG, "Cannot execute shell command: " + COMMAND_PS_NAME + packageName + " - " + e.getLocalizedMessage());
//            e.printStackTrace();
//        }

        return processFolder;
    }

    private float getRamUsage()
    {
        float ramUsage = 0;


        return ramUsage;
    }
}
