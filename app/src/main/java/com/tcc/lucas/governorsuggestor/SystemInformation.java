package com.tcc.lucas.governorsuggestor;

import android.app.ActivityManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

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

    private Context mContext;
    private List<String> mAvailableGovernorsList;
    private GovernorRanker mGovernorRanker;
    private CPUInformation mCPUInformation;

    public SystemInformation(Context context)
    {
        mContext = context;

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo memoryInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(memoryInfo);

        mAvailableGovernorsList = getAvailableGovernors();
        mCPUInformation = new CPUInformation();

        setApplicationRanker(new GovernorRanker(memoryInfo));
    }

    public void collectSystemInformation()
    {
        List<ApplicationInfo> deviceAppsList = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        getGovernorRanker().rankApplication(deviceAppsList);
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

            ProcessCommand.runRootCommand(changeGovernorArgs, false);
        }

        else
        {
            Toast.makeText(mContext, "This governor is not available on this device.", Toast.LENGTH_LONG).show();
            Log.d(LOG_TAG, "The specified governor ( " + mAvailableGovernorsList.get(governorPos) + ") is not available on this system.");
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
