package com.tcc.lucas.governorsuggestor;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by Lucas on 9/6/2015.
 */
public class GovernorRanker
{
    private final String LOG_TAG = getClass().getSimpleName();
    private final String mDeviceModel = Build.MODEL;

    private double mTotalMemoryMB;
    private List<Governor> mGenericGovernorList;
    private List<Governor> mDeviceGovernorList;
    private ProcessDump mProcessDump;
    private BatteryDump mBatteryDump;

    public GovernorRanker(ActivityManager.MemoryInfo memoryInfo)
    {
        mTotalMemoryMB = memoryInfo.totalMem / (1024 * 1024);

        initializeDeviceGovernorList();
        initializeGenericGovernorList();
    }

    public List<Application> rankApplication(List<ApplicationInfo> applicationList)
    {
        initializeDumpServices(applicationList);

        List<Application> rankedApplicationsList = new ArrayList<>();

        for (ApplicationInfo app : applicationList)
        {
            if((app.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
            {
                Application newApplication = collectApplicationStatistics(app);

                if(newApplication != null)
                    rankedApplicationsList.add(newApplication);
            }
        }

        for (Application app : rankedApplicationsList)
        {
            rankGovernors(app);
        }

        return rankedApplicationsList;
    }

    private void initializeDumpServices(List<ApplicationInfo> deviceAppsList)
    {
        mBatteryDump = new BatteryDump();

        for (ApplicationInfo appInfo : deviceAppsList)
        {
            if((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0)
                mBatteryDump.dump(appInfo.packageName);
        }

        long requestedTime = TimeUnit.MILLISECONDS.toHours(mBatteryDump.getRunTime());
        mProcessDump = new ProcessDump(requestedTime);
    }

    private void rankGovernors(Application application)
    {
        for(Governor governor : getGovernorList())
        {
            double performanceScore = (application.getCPUPercent() * governor.getCPUOverall() +
                    application.getRAMPercent() * governor.getRAMOverall()) / 2;

            double batteryScore = governor.getBatteryOverall() * application.getBatteryPercent();

            governor.setPerformanceScore(governor.getPerformanceScore() + performanceScore);
            governor.setBatteryScore(governor.getBatteryScore() + batteryScore);
        }
    }

    private Application collectApplicationStatistics(ApplicationInfo applicationInfo)
    {
        Application rankedApplication = null;

        if (applicationInfo != null)
        {
            rankedApplication = new Application(applicationInfo.packageName);

            double ramUsage = 0;
            double cpuUsage = 0;
            double batteryUsage = 0;

            MemoryStats processStats = (MemoryStats) mProcessDump.get(applicationInfo.packageName);
            BatteryStats batteryStats = (BatteryStats) mBatteryDump.get(applicationInfo.packageName);

            // RAM Usage over a period of 24 hours
            if(processStats != null && processStats != null)
                ramUsage = (100 * processStats.getAvgPss()) / mTotalMemoryMB;

            rankedApplication.setRAMPercent(ramUsage);

            // CPU and Battery Usage since last recharging
            if(batteryStats != null)
            {
                cpuUsage = batteryStats.getCPUPercent();
                batteryUsage = batteryStats.getBatteryUsed();
            }

            rankedApplication.setCPUPercent(cpuUsage);
            rankedApplication.setBatteryPercent(batteryUsage);
        }

        return rankedApplication;
    }

    private void initializeGenericGovernorList()
    {
        mGenericGovernorList = new ArrayList<>();

        Governor governor = new Governor(null, Definitions.Governor.Performance,
                1.061129197,
                1.022295609,
                1.070707942,
                1.236168976,
                1.042315211,
                1.047592519,
                1.16087308,
                1.146353181,
                1.014718651,
                1.073083287,
                1.024488519,
                1.061675718,
                1,
                1.045214855,
                1.101889397,
                1.179940776,
                1.069829205,
                1.033056916
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Interactive,
                1.074887722,
                1.001967763,
                1.038501337,
                1.178325744,
                1.038962006,
                1.027415816,
                1.091371405,
                1.056825278,
                1,
                1.036017253,
                1.011931307,
                1.043117089,
                1.098621421,
                1.045004488,
                1.072278985,
                1.077144945,
                1.03164379,
                1.018311494
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
                1.00367719,
                1,
                1.165225549,
                1,
                1,
                1,
                1,
                1
        );
        mGenericGovernorList.add(governor);

        governor = new Governor(null, Definitions.Governor.Conservative,
                1.027716061,
                1.008042161,
                1.010665474,
                1.155199351,
                1.006756931,
                1.000201944,
                1.051621708,
                1.04617982,
                1.018481002,
                1.002427448,
                1,
                1.048651302,
                1.144529869,
                1.019653526,
                1.044189149,
                1.048460755,
                1.003234414,
                1.010662836
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
                1.031042636
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Conservative,
                1.060159003,
                1.022954293,
                1.023639113,
                1.386574912,
                1.016249245,
                1.006787163,
                1.108270677,
                1.109057014,
                1.02687836,
                1.005228433,
                1.000510048,
                1.167286245,
                1.062175859,
                1.047089056,
                1.11112653,
                1.108701199,
                1.007234647,
                1.035466402
        );
        mDeviceGovernorList.add(governor);

        governor = new Governor(Definitions.DEVICE_MOTO_MAXX, Definitions.Governor.Interactive,
                1.044040106,
                1.004590859,
                1.025705645,
                1.389049861,
                1.003812709,
                1.008920085,
                1.093233339,
                1.087274016,
                1,
                1.004091247,
                1.009486892,
                1.043838863,
                1.052988768,
                1.030181657,
                1.110000925,
                1.098877265,
                1.003548837,
                1.012279765
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
                1.008160767,
                1,
                1.10671843,
                1,
                1,
                1,
                1,
                1
        );
        mDeviceGovernorList.add(governor);
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

                double thisTotal = governor.getPerformanceScore() + governor.getBatteryScore();
                double t1Total = t1.getPerformanceScore() + t1.getBatteryScore();

                if(thisTotal < t1Total)
                    retVal = 1;
                else if(thisTotal > t1Total)
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
