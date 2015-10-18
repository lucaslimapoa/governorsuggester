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
import java.util.Comparator;
import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class GovernorRanker
{
    private final String LOG_TAG = getClass().getSimpleName();

    private List<ApplicationInfo> mDeviceAppsList;
    private CpuUsage mCpuUsage;
    private MemoryUsage mMemUsage;
    private List<Governor> mGovernorList;
    private double mTotalRunTime;

    public GovernorRanker(List<ApplicationInfo> mDeviceAppsList, CpuUsage cpuUsage, MemoryUsage memUsage)
    {
        this.mDeviceAppsList = mDeviceAppsList;
        this.mCpuUsage = cpuUsage;
        this.mMemUsage = memUsage;

        this.setGovernorList(initializeGovernorList());
    }

    public List<Application> rankApplication(List<UsageStats> applicationList)
    {
        List<Application> rankedApplicationsList = new ArrayList<Application>();

        for (UsageStats app : applicationList)
        {
            Application newApplication = collectApplicationStatistics(app);

            if(newApplication != null)
            {
                rankedApplicationsList.add(newApplication);
                mTotalRunTime += newApplication.getRunTime();
            }
        }

        for (Application app : rankedApplicationsList)
        {
            rankApplication(app);
        }

        return rankedApplicationsList;
    }

    private void rankApplication(Application application)
    {
        double runTimePercent = 1 + (application.getRunTime() / mTotalRunTime);

        for(Governor governor : getGovernorList())
        {
            double governorScore = ( ( application.getCPUPercent() * governor.getCPUOverall() +
                    application.getRAMPercent() * governor.getRAMOverall() ) * runTimePercent ) *
                    governor.getBatteryOverall();

            governor.setTotalScore(governor.getTotalScore() + governorScore);
        }
    }

    private Application collectApplicationStatistics(UsageStats applicationStats)
    {
        Application rankedApplication = null;

        ApplicationInfo applicationInfo = findApplicationByPackage(applicationStats.getPackageName());

        if (applicationInfo != null)
        {
            String processId = getProcessFolderByPackage(applicationInfo.packageName);

            if (processId != null)
            {
                rankedApplication = new Application();
                ProcessUsage appProcessUsage = new ProcessUsage(processId);

                rankedApplication.setName(applicationInfo.packageName);

                // RAM Information
                rankedApplication.setVirtualRAM(Long.parseLong(appProcessUsage.get(ProcessUsage.VMSIZE)));
                rankedApplication.setPhysicalRAM(Long.parseLong(appProcessUsage.get(ProcessUsage.VMRSS)));
                rankedApplication.setRAMPercent(100 * ( rankedApplication.getPhysicalRAM() / mMemUsage.getMemTotal() ));

                // Network Information
                rankedApplication.setBytesReceived(TrafficStats.getUidRxBytes(applicationInfo.uid));
                rankedApplication.setBytesReceived(TrafficStats.getUidTxBytes(applicationInfo.uid));

                // Run Time Information
                rankedApplication.setRunTime(applicationStats.getTotalTimeInForeground());

                // CPU Information
                float totalProcessCpuTime = Float.parseFloat(appProcessUsage.get(ProcessUsage.CPU_CTIME));
                totalProcessCpuTime += Float.parseFloat(appProcessUsage.get(ProcessUsage.CPU_STIME));
                totalProcessCpuTime += Float.parseFloat(appProcessUsage.get(ProcessUsage.CPU_UTIME));
                rankedApplication.setCpuUsed(totalProcessCpuTime);

                float cpuPercentageUsed = calculateCpuInformation(rankedApplication);
                rankedApplication.setCPUPercent(cpuPercentageUsed);
            }
        }

        return rankedApplication;
    }

    private ArrayList<Governor> initializeGovernorList()
    {
        ArrayList<Governor> governorList = new ArrayList<>();

        Governor governor = new Governor(Definitions.Governor.Interactive,
                1.017282963, 1, 1.048940403, 1.143770228, 1.008518406, 1.022263203, 1.090920518,
                1.046822325, 1.000734005,  1,  1, 1.015077821, 1.115008832, 1.009357346, 1.060484676, 1.065649428,
                1, 1);
        governorList.add(governor);

        governor = new Governor(Definitions.Governor.Performance,
                1.063041235, 1.023471185, 1.042056839, 1.159493266, 1.010925645, 1, 1.134439702,
                1.13934611, 1, 1.014041903, 1.022156009, 1.024754383, 1 /*Change later on*/,
                1.046318394, 1.059561684, 1.137314813, 1.01230366, 1.018654267);
        governorList.add(governor);

        governor = new Governor(Definitions.Governor.Ondemand,
                1, 1.002211365, 1, 1, 1, 1.001174029, 1, 1, 1.000815494, 1.153684647, 1.281459649,
                1, 1.165651602, 1,  1, 1, 1.138173845, 1.200874317);
        governorList.add(governor);

        // TODO: Add data for Conservative Governor

        return governorList;
    }

    private ApplicationInfo findApplicationByPackage(String packageName)
    {
        ApplicationInfo applicationInfo = null;

        for (int i = 0; i < mDeviceAppsList.size(); i++)
        {
            String packageNameIterator = mDeviceAppsList.get(i).packageName;

            if (packageNameIterator != null && packageNameIterator.equals(packageName))
            {
                applicationInfo = mDeviceAppsList.get(i);
                break;
            }
        }

        return applicationInfo;
    }

    private String getProcessFolderByPackage(final String packageName)
    {
        String processFolder = null;

        File procFolder = new File(Definitions.FOLER_PROC);

        List<File> procFolderFiles = Arrays.asList(procFolder.listFiles());
        Collections.sort(procFolderFiles);

        for (File iterator : procFolderFiles)
        {
            if (iterator.isDirectory() == false || iterator.listFiles() == null)
                continue;

            List<File> subfiles = Arrays.asList(iterator.listFiles());

            for (int i = 0; i < subfiles.size(); i++)
            {
                String fileName = subfiles.get(i).getName();

                if (fileName != null && fileName.equals(Definitions.FILE_PROCESS_CMDLINE))
                {
                    FileInputStream fileInputStream = null;

                    try
                    {
                        fileInputStream = new FileInputStream(subfiles.get(i));
                        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));

                        String cmdlineText = bufferedReader.readLine();

                        if (cmdlineText != null)
                        {
                            if (cmdlineText.contains(packageName))
                                return iterator.getAbsolutePath();
                        }

                        break;
                    } catch (FileNotFoundException e)
                    {
                        Log.e(LOG_TAG, "File not found- " + e.getLocalizedMessage());
                        e.printStackTrace();
                    } catch (IOException e)
                    {
                        Log.e(LOG_TAG, "Cannot open file - " + e.getLocalizedMessage());
                        e.printStackTrace();
                    }
                }
            }
        }

        return processFolder;
    }

    private float calculateCpuInformation(Application app)
    {
        float returnValue = 0;

        returnValue = 100 * ( app.getCpuUsed() / mCpuUsage.getUser() );

        return returnValue;
    }

    public List<Governor> getGovernorList()
    {
        Collections.sort(mGovernorList, new Comparator<Governor>()
        {
            @Override
            public int compare(Governor governor, Governor t1)
            {
                int retVal = 0;

                if(governor.getTotalScore() < t1.getTotalScore())
                    retVal = 1;
                else if(governor.getTotalScore() > t1.getTotalScore())
                    retVal = -1;

                return  retVal;
            }
        });

        return mGovernorList;
    }

    public void setGovernorList(List<Governor> mGovernorList)
    {
        this.mGovernorList = mGovernorList;
    }
}
