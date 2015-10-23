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

    private ProcessDump mProcessDump;

    public GovernorRanker(List<ApplicationInfo> mDeviceAppsList)
    {
        this.mDeviceAppsList = mDeviceAppsList;
        this.mCpuUsage = new CpuUsage();
        this.mMemUsage = new MemoryUsage();

        initializeDeviceGovernorList();
        initializeGenericGovernorList();

        mProcessDump = new ProcessDump();
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
                1.061129197, 1.022295609, 1.036732433, 1.206909872, 1.003236741,
                1, 1.127975357, 1.150768885, 1.014718651, 1.048528467, 1.035875326,
                1.061675718, 1, 1.045214855, 1.062522414, 1.16463485, 1.045399777, 1.031276488
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Interactive,
                1.008720861, 1.001967763, 1.041249414, 1.189767981,
                1.006679704, 1.013895284, 1.091371405, 1.056825278,
                1, 1.036017253, 1, 1.043117089, 1.085472496, 1.005953383,
                1.063440252, 1.077144945, 1.03164379, 1
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Ondemand,
                1, 1, 1, 1, 1, 1.003656654, 1, 1, 1.009385207,
                1, 1.094098568, 1, 1.151279468, 1, 1, 1, 1,
                1.023619823
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Conservative,
                1.027716061, 1.008042161, 1.025743597, 1.152490403,
                1.006104577, 1.008274872, 1.051621708, 1.064055879,
                1.018481002, 1.030961767, 1.011114627, 1.048651302,
                1.103858553, 1.019653526, 1.04822298,  1.058844172,
                1.028271314, 1.008921004
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
                1.047637604,
                1.0649127,
                1,
                1,
                1.065033784,
                1,
                1,
                1.046300159,
                1.019710378,
                1.02175108,
                1,
                1.029078733,
                1.020901913,
                1.002051444,
                1.038956232,
                1.018096578
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Conservative,
                1.002719239,
                1.002524318,
                1.000121773,
                1,
                1.008110983,
                1.010462309,
                1,
                1.036833856,
                1.015930176,
                1.000289757,
                1.003117458,
                1,
                1.282823107,
                1.002638014,
                1,
                1,
                1.000532063,
                1
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Interactive,
                1.095551023,
                1.065394375,
                1.161617146,
                1.210236785,
                1.199273114,
                1.070553519,
                1.191047297,
                1.017345873,
                1.018554688,
                1.117662949,
                1,
                1.118178249,
                1.17795995,
                1.082984535,
                1.154034512,
                1.059618116,
                1.113537522,
                1.033098922
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_LG_G3, Definitions.Governor.Ondemand,
                1,
                1,
                1,
                1.007127481,
                1.009041752,
                1.00742198,
                1.002533784,
                1.03876698,
                1.014038086,
                1,
                1.004961116,
                1.016725559,
                1.282780411,
                1,
                1.001146545,
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
                1.035912268,
                1.169775297,
                1.194235359,
                1.016294662,
                1.32142171,
                1.339674884,
                1.04563998,
                1.021873276,
                1.671934501,
                1.052896725,
                1,
                1.059256172,
                1.098617912,
                1.432880785,
                1.023051015,
                1.438656858
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Conservative,
                1.002786341,
                1.063828349,
                1.016208725,
                1.050998678,
                1.178745181,
                1.041967241,
                1.001200768,
                1.001284999,
                1.032317695,
                1.000581529,
                1.575352247,
                1.005982368,
                1.058894161,
                1.000637427,
                1.064382409,
                1,
                1.002340636,
                1.36079141
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Interactive,
                1.102489922,
                1,
                1,
                1,
                1,
                1,
                1,
                1.096983022,
                1,
                1.002373334,
                1,
                1,
                1.023551878,
                1.02324156,
                1,
                1.061698543,
                1,
                1
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_NEXUS_5, Definitions.Governor.Ondemand,
                1,
                1.065599481,
                1.014039528,
                1.051990013,
                1.178079677,
                1.038190614,
                1.003458213,
                1,
                1.032126466,
                1,
                1.576351866,
                1.011020151,
                1.060065968,
                1,
                1.062978579,
                1.000178377,
                1.00180762,
                1.363312767
        );
        mDeviceGovernorList.add(governor);
    }

    private void createMotoMaxxGovernors()
    {
        Governor governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Performance,
                1.190743123,
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
                1.125135562,
                1.115536436,
                1.132048992,
                1.069270807,
                1
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Conservative,
                1.188660173,
                1.022954293,
                1.059022177,
                1.386574912,
                1.016249245,
                1.006787163,
                1.108270677,
                1.15100202,
                1.02687836,
                1.074207782,
                1.000510048,
                1.167286245,
                1.00266722,
                1.124108145,
                1.121950843,
                1.131666241,
                1.069333068,
                1.004290576
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Interactive,
                1,
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
                1,
                1.110000925,
                1.098877265,
                1.003548837,
                1.17976032
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Ondemand,
                1.121209337,
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
                1.073555433,
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
