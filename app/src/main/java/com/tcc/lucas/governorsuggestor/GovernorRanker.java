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

    public GovernorRanker(List<ApplicationInfo> mDeviceAppsList)
    {
        this.mDeviceAppsList = mDeviceAppsList;
        this.mCpuUsage = new CpuUsage();
        this.mMemUsage = new MemoryUsage();

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
            rankGovernors(app);
        }

        return rankedApplicationsList;
    }

    private void rankGovernors(Application application)
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

        Governor governor = new Governor(Definitions.Governor.Performance,
                1.061129197, 1.022295609, 1.036732433, 1.206909872, 1.003236741,
                1, 1.127975357, 1.150768885, 1.014718651, 1.048528467, 1.035875326,
                1.061675718, 1, 1.045214855, 1.062522414, 1.16463485, 1.045399777, 1.031276488
        );
        governorList.add(governor);

        governor = new Governor(Definitions.Governor.Interactive,
                1.008720861, 1.001967763, 1.041249414, 1.189767981,
                1.006679704, 1.013895284, 1.091371405, 1.056825278,
                1, 1.036017253, 1, 1.043117089, 1.085472496, 1.005953383,
                1.063440252, 1.077144945, 1.03164379, 1
        );
        governorList.add(governor);

        governor = new Governor(Definitions.Governor.Ondemand,
                1, 1, 1, 1, 1, 1.003656654, 1, 1, 1.009385207,
                1, 1.094098568, 1, 1.151279468, 1, 1, 1, 1,
                1.023619823
        );
        governorList.add(governor);

        governor = new Governor(Definitions.Governor.Conservative,
                1.027716061, 1.008042161, 1.025743597, 1.152490403,
                1.006104577, 1.008274872, 1.051621708, 1.064055879,
                1.018481002, 1.030961767, 1.011114627, 1.048651302,
                1.103858553, 1.019653526, 1.04822298,  1.058844172,
                1.028271314, 1.008921004
        );
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
