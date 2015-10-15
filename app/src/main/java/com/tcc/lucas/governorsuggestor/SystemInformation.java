package com.tcc.lucas.governorsuggestor;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class SystemInformation
{
    private final String LOG_TAG = getClass().getSimpleName();

    private Context mCurrentContext;
    private List<Application> mRankedAppsList;
    private ApplicationRanker mApplicationRanker;
    private UsageStatsManager mUsageStatsManager;
    private float mTotalReceivedBytes;
    private float mTotalTransmittedBytes;
    private CpuUsage mCpuUsage;
    private MemoryUsage mMemUsage;
    private List<String> mAvailableGovernorsList;

    public String DeviceModel = Build.MODEL;
    public String DeviceBrand = Build.BRAND;

    public SystemInformation(Context context)
    {
        mCurrentContext = context;
        mUsageStatsManager = (UsageStatsManager) mCurrentContext.getSystemService(Context.USAGE_STATS_SERVICE);

        mTotalReceivedBytes = TrafficStats.getTotalRxBytes();
        mTotalTransmittedBytes = TrafficStats.getTotalTxBytes();

        mCpuUsage = new CpuUsage();
        mMemUsage = new MemoryUsage();
        mAvailableGovernorsList = getAvailableGovernors();

        List<ApplicationInfo> deviceAppsList = mCurrentContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        setApplicationRanker(new ApplicationRanker(deviceAppsList, mCpuUsage, mMemUsage));
    }

    public float getTotalReceivedBytes()
    {
        return mTotalReceivedBytes;
    }

    public float getTotalTransmittedBytes()
    {
        return mTotalTransmittedBytes;
    }

    public void setTotalReceivedBytes(float mTotalReceivedBytes)
    {
        this.mTotalReceivedBytes = mTotalReceivedBytes;
    }

    public void setTotalTransmittedBytes(float mTotalTransmittedBytes)
    {
        this.mTotalTransmittedBytes = mTotalTransmittedBytes;
    }

    public void collectSystemInformation()
    {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -3); // Hardcoded value for now. Will start looking for usage stats starting 3 months ago

        List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, calendar.getTimeInMillis(), System.currentTimeMillis());
        mRankedAppsList = getApplicationRanker().rankApplication(usageStatsList);
    }

    public ApplicationRanker getApplicationRanker()
    {
        return mApplicationRanker;
    }

    public void setApplicationRanker(ApplicationRanker mApplicationRanker)
    {
        this.mApplicationRanker = mApplicationRanker;
    }

    public void setSystemCPUGovernor(Definitions.Governor governor)
    {
        try
        {
            String governorName = governor.name().toLowerCase();

            String[] args = {"sudo /system/bin/echo " + governorName + " >", Definitions.FOLDER_SYSTEM_GOVERNOR + Definitions.FILE_SYSTEM_GOVERNOR};
            ProcessBuilder changeGovernorCommand = new ProcessBuilder(args);

            Process process = changeGovernorCommand.start();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot change the specified governor");
            e.printStackTrace();
        }
    }

    private List<String> getAvailableGovernors()
    {
        List<String> availableGovernorsList = new ArrayList<>();

        try
        {
            File statusFile = new File(Definitions.FOLDER_SYSTEM_GOVERNOR + Definitions.FILE_SYSTEM_AVAILABLE_GOVERNORS);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

            String[] governorsList = bufferedReader.readLine().split(" ");

            if(governorsList.length > 0)
                availableGovernorsList = Arrays.asList(governorsList);
        }

        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, "Cannot find scaling_available_governors file");
            e.printStackTrace();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot open scaling_available_governors file");
            e.printStackTrace();
        }

        return availableGovernorsList;
    }
}
