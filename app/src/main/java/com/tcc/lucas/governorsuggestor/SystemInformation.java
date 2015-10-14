package com.tcc.lucas.governorsuggestor;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.Build;

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
}
