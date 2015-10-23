package com.tcc.lucas.governorsuggestor;

import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;
import android.os.Build;
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
    private List<Governor> mGenericGovernorList;
    private List<Governor> mDeviceGovernorList;
    private double mTotalRunTime;
    private String mDeviceModel = Build.MODEL;

    public GovernorRanker(List<ApplicationInfo> mDeviceAppsList)
    {
        this.mDeviceAppsList = mDeviceAppsList;
        this.mCpuUsage = new CpuUsage();
        this.mMemUsage = new MemoryUsage();

        initializeDeviceGovernorList();
        initializeGenericGovernorList();
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

    private void initializeGenericGovernorList()
    {
        mGenericGovernorList = new ArrayList<>();

        Governor governor = new Governor(null, Definitions.Governor.Performance,
                1.061129197,
                1.022295609,
                1.074873561,
                1.23080642,
                1.04040379,
                1.043147074,
                1.158962883,
                1.14439472,
                1.014718651,
                1.07218279,
                1.024488519,
                1.061675718,
                1,
                1.045214855,
                1.10003069,
                1.177248779,
                1.068910466,
                1.022157814
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Interactive,
                1.074353064,
                1.001967763,
                1.043387496,
                1.173926707,
                1.04623254,
                1.024663496,
                1.091371405,
                1.056825278,
                1,
                1.036017253,
                1.115753237,
                1.043117089,
                1.096459925,
                1.044688938,
                1.073711024,
                1.077144945,
                1.03164379,
                1.0818894
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Ondemand,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1.009385207,
                1,
                1.082071744,
                1,
                1.162933011,
                1,
                1,
                1,
                1,
                1.045568787
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Conservative,
                1.027716061,
                1.008042161,
                1.026170236,
                1.155391849,
                1.007277486,
                1.002968074,
                1.051621708,
                1064055879,
                1.018481002,
                1.001918213,
                1,
                1.048651302,
                1.11503209,
                1.019653526,
                1.049314081,
                1.058844172,
                1.002787596,
                1
        );
        mGenericGovernorList.add(governor);
    }

    private void initializeDeviceGovernorList()
    {
        mDeviceGovernorList = new ArrayList<>();

        createLGG3Governors();
        createNexus5Governors();
        createMotoMaxxGovernors();
    }

    private void createLGG3Governors()
    {
        Governor governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Performance,
                1.033612811,
                1.022732079,
                1.156514453,
                1.151161843,
                1.117665178,
                1.111696607,
                1.412668919,
                1.101273624,
                1,
                1.129139767,
                1.019710378,
                1.02175108,
                1,
                1.029078733,
                1.133000965,
                1.20467098,
                1.123364486,
                1.018096578
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Conservative,
                1.002719239,
                1.002524318,
                1.003665689,
                1,
                1.001174342,
                1,
                1,
                1.019155711,
                1.015930176,
                1.000289757,
                1.003117458,
                1,
                1.282823107,
                1.002638014,
                1.000522676,
                1,
                1.000532063,
                1
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Interactive,
                1.095551023,
                1.065394375,
                1.072978634,
                1.107052589,
                1.108501463,
                1.054211577,
                1.191047297,
                1,
                1.018554688,
                1.117662949,
                1,
                1.118178249,
                1.17795995,
                1.082984535,
                1.085216307,
                1.059618116,
                1.113537522,
                1.033098922
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Ondemand,
                1,
                1,
                1,
                1.000978394,
                1,
                1.001676647,
                1.002533784,
                1.021055875,
                1.014038086,
                1,
                1.004961116,
                1.016725559,
                1.282780411,
                1,
                1,
                1.002114565,
                1,
                1.006282232
        );
        mDeviceGovernorList.add(governor);
    }

    private void createNexus5Governors()
    {
        Governor governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Performance,
                1.094142756,
                1.088962198,
                1.052553616,
                1.136282468,
                1.029075708,
                1,
                1.154274736,
                1.269249778,
                1.04563998,
                1.021873276,
                1.061308355,
                1.052896725,
                1,
                1.059256172,
                1.05257993,
                1.278366862,
                1.023051015,
                1.057220708
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Conservative,
                1.002786341,
                1.063828349,
                1.000441846,
                1.009232955,
                1,
                1.017281811,
                1.001200768,
                1.001284999,
                1.032317695,
                1.000581529,
                1,
                1.005982368,
                1.121178237,
                1.000637427,
                1,
                1,
                1.002340636,
                1
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Interactive,
                1.102489922,
                1,
                1.052919591,
                1,
                1.019972132,
                1.060868679,
                1,
                1.096983022,
                1,
                1.002373334,
                1.024807373,
                1,
                1.083757123,
                1.02324156,
                1.028067337,
                1.061698543,
                1,
                1.016239782
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Ondemand,
                1,
                1.065599481,
                1,
                1.00836039,
                1.000743149,
                1.018862153,
                1.003458213,
                1,
                1.032126466,
                1,
                1.000634537,
                1.011020151,
                1.122418969,
                1,
                1.000130497,
                1.000178377,
                1.00180762,
                1.001852861
        );
        mDeviceGovernorList.add(governor);
    }

    private void createMotoMaxxGovernors()
    {
        Governor governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Performance,
                1.062016775,
                1.022248007,
                1.043951613,
                1.384039599,
                1.003546099,
                1.014969842,
                1.099373625,
                1.130428909,
                1.018752344,
                1.069800057,
                1,
                1.131090175,
                1,
                1.048046079,
                1.115536436,
                1.132048992,
                1.069270807,
                1
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Conservative,
                1.060159003,
                1.022954293,
                1.059022177,
                1.386574912,
                1.016249245,
                1.006787163,
                1.108270677,
                1.15100202,
                1.02687836,
                1.003485622,
                1.000510048,
                1.167286245,
                1.00266722,
                1.047089056,
                1.121950843,
                1.131666241,
                1.005665687,
                1.004290576
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Interactive,
                1.047537087,
                1.004590859,
                1.025705645,
                1.389049861,
                1.003812709,
                1.008920085,
                1.093233339,
                1.087274016,
                1,
                1.004091247,
                1.282464552,
                1.043838863,
                1.052988768,
                1.032450155,
                1.110000925,
                1.098877265,
                1.003548837,
                1.17976032
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Ondemand,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1,
                1.001500188,
                1,
                1.212587983,
                1,
                1.10671843,
                1,
                1,
                1,
                1,
                1.118138778
        );
        mDeviceGovernorList.add(governor);
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


    private List<Governor> getDeviceGovernorList(String device)
    {
        List<Governor> deviceGovernorList = new ArrayList<>();

        for (Governor governor : mDeviceGovernorList)
        {
            if(governor.getDevice().equalsIgnoreCase(device))
                deviceGovernorList.add(governor);
        }

        return deviceGovernorList;
    }

    public List<Governor> getGovernorList()
    {
        Comparator descendingScoreComparator = new Comparator<Governor>()
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
        };

        List<Governor> deviceGovernorList = getDeviceGovernorList(mDeviceModel);

        if(deviceGovernorList.size() > 0)
        {
            Collections.sort(deviceGovernorList, descendingScoreComparator);
            return deviceGovernorList;
        }

        else
        {
            Collections.sort(mGenericGovernorList, descendingScoreComparator);
            return mGenericGovernorList;
        }
    }
}
