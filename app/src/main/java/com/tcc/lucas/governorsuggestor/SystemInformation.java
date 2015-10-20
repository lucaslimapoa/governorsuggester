package com.tcc.lucas.governorsuggestor;

import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
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
public class SystemInformation extends AsyncTask<Void, Void, Void>
{
    private final String LOG_TAG = getClass().getSimpleName();

    private GovernorRanker mGovernorRanker;
    private UsageStatsManager mUsageStatsManager;
    private float mTotalReceivedBytes;
    private float mTotalTransmittedBytes;
    private List<String> mAvailableGovernorsList;
    private CPUInformation mCPUInformation;

    public SystemInformation(Context context)
    {
        mUsageStatsManager = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);

        mTotalReceivedBytes = TrafficStats.getTotalRxBytes();
        mTotalTransmittedBytes = TrafficStats.getTotalTxBytes();

        mAvailableGovernorsList = getAvailableGovernors();
        mCPUInformation = new CPUInformation();

        List<ApplicationInfo> deviceAppsList = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        setApplicationRanker(new GovernorRanker(deviceAppsList));
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
        calendar.add(Calendar.DAY_OF_MONTH, -1); // Hardcoded value for now. Will start looking for usage stats starting 3 months ago

        List<UsageStats> usageStatsList = mUsageStatsManager.queryUsageStats(UsageStatsManager.INTERVAL_MONTHLY, calendar.getTimeInMillis(), System.currentTimeMillis());
        getGovernorRanker().rankApplication(usageStatsList);
    }

    public GovernorRanker getGovernorRanker()
    {
        return mGovernorRanker;
    }

    public void setApplicationRanker(GovernorRanker mGovernorRanker)
    {
        this.mGovernorRanker = mGovernorRanker;
    }

    public void setSystemCPUGovernor(Definitions.Governor governor)
    {
        int governorPos = governorExists(governor.name());

        if (governorPos != -1)
        {
            String echo = "echo ";
            String writeGovernor = echo + mAvailableGovernorsList.get(governorPos) + " > ";
            String scalingGovernorFile = Definitions.FOLDER_SYSTEM_CPU + Definitions.FILE_SYSTEM_GOVERNOR;

            String[] changeGovernorArgs = {writeGovernor + scalingGovernorFile};

            runRootCommand(changeGovernorArgs);
        }

        else
        {
            Log.d(LOG_TAG, "The specified governor ( " + mAvailableGovernorsList.get(governorPos) + ") is not available on this system.");
        }
    }

    public void runRootCommand(String[] commandList)
    {
        String su = "su ";

        try
        {
            Process rootProcess = Runtime.getRuntime().exec(su);
            DataOutputStream outputStream = new DataOutputStream(rootProcess.getOutputStream());

            for (String cmd : commandList)
            {
                outputStream.writeBytes(cmd + "\n");
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot run the command as root");
            e.printStackTrace();
        }
    }

    private int governorExists(String governor)
    {
        int retVal = -1;

        for(int i = 0; i < mAvailableGovernorsList.size(); i++)
        {
            String availableGovernor = mAvailableGovernorsList.get(i);

            if(availableGovernor.equalsIgnoreCase(governor))
            {
                retVal = i;
                break;
            }
        }

        return retVal;
    }

    private List<String> getAvailableGovernors()
    {
        List<String> availableGovernorsList = new ArrayList<>();

        try
        {
            File statusFile = new File(Definitions.FOLDER_SYSTEM_CPU + Definitions.FILE_SYSTEM_AVAILABLE_GOVERNORS);
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

    public String getCurrentGovernor()
    {
        String currentGovernor = "";

        try
        {
            File statusFile = new File(Definitions.FOLDER_SYSTEM_CPU + Definitions.FILE_SYSTEM_GOVERNOR);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

            String governor = bufferedReader.readLine();

            if(governor.length() > 0)
                currentGovernor = governor;
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

        return currentGovernor;
    }

    public CPUInformation getCPUInformation()
    {
        return mCPUInformation;
    }

    @Override
    protected Void doInBackground(Void... params)
    {
        collectSystemInformation();
        return null;
    }
}
