package com.tcc.lucas.governorsuggestor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AppOpsManager;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
{
    // UI Elements
    private Toolbar mToolbar;
    private ListView mGovernorListView;
    private TextView mMinCPUFrequency;
    private TextView mMaxCPUFrequency;
    private TextView mCurrentCPUFrequency;
    private TextView mCurrentGovernor;
    private ProgressBar mListViewProgressBar;

    // Logic Variables
    private SystemInformation mSystemInformation;
    private GovernorArrayAdapter mGovernorArrayAdapter;
    private Handler mUIHandler;
    private int mIntervalRate = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart()
    {
        super.onStart();

        init();
        initUI();
        updateUI();

        if (isUsageAccessPermissionEnabled() == true)
        {
            mGovernorListView.setVisibility(View.INVISIBLE);
            mListViewProgressBar.setVisibility(View.VISIBLE);

            mSystemInformation.execute();
        }

        else
            requestUsagePermission();
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }

    private void init()
    {
        mSystemInformation = new SystemInformation(getApplicationContext())
        {
            @Override
            protected void onPostExecute(Void aVoid)
            {
                super.onPostExecute(aVoid);

                List<Governor> governorList = mSystemInformation.getGovernorRanker().getGovernorList();
                mGovernorArrayAdapter = new GovernorArrayAdapter(getApplicationContext(), governorList);

                mListViewProgressBar.setVisibility(View.INVISIBLE);
                mGovernorListView.setAdapter(mGovernorArrayAdapter);
                mGovernorListView.setVisibility(View.VISIBLE);
            }
        };

        mUIHandler = new Handler();
    }

    private void initUI()
    {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mMinCPUFrequency = (TextView) findViewById(R.id.cpuMinimumFrequencyValueTextView);
        mMinCPUFrequency.setText(mSystemInformation.getCPUInformation().getMinCPUFreq());

        mMaxCPUFrequency = (TextView) findViewById(R.id.cpuMaximumFrequencyValueTextView);
        mMaxCPUFrequency.setText(mSystemInformation.getCPUInformation().getMaxCPUFreq());

        mCurrentCPUFrequency = (TextView) findViewById(R.id.cpuCurrentFrequencyTextView);
        mCurrentCPUFrequency.setText(mSystemInformation.getCPUInformation().getCurrentCPUFreq());

        mCurrentGovernor = (TextView) findViewById(R.id.governorValueTextView);
        mCurrentGovernor.setText(mSystemInformation.getCurrentGovernor());

        mListViewProgressBar = (ProgressBar) findViewById(R.id.listViewProgressBar);

        mGovernorListView = (ListView) findViewById(R.id.governorListView);
        mGovernorListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Definitions.Governor governorToSet = mGovernorArrayAdapter.getItem(position).getName();
                mSystemInformation.setSystemCPUGovernor(governorToSet);
            }
        });
    }

    private void updateUI()
    {
        mUIHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                String currentCPUFreq = mSystemInformation.getCPUInformation().getCurrentCPUFreq();
                mCurrentCPUFrequency.setText(currentCPUFreq);

                mCurrentGovernor.setText(mSystemInformation.getCurrentGovernor());

                mUIHandler.postDelayed(this, mIntervalRate);
            }
        }, mIntervalRate);
    }

    private void requestUsagePermission()
    {
        final Context context = getApplicationContext();

        Toast.makeText(context, R.string.usage_access, Toast.LENGTH_LONG).show();

        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener()
        {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                switch (which)
                {
                    case DialogInterface.BUTTON_POSITIVE:
                        Intent openUsageAccessSettings = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
                        openUsageAccessSettings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                        context.startActivity(openUsageAccessSettings);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage("Do you want go give Usage Statistics permission?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener)
                .show();
    }

    private boolean isUsageAccessPermissionEnabled()
    {
        boolean retVal = false;

        try
        {
            Context context = getApplicationContext();

            PackageManager packageManager = context.getPackageManager();
            ApplicationInfo applicationInfo = packageManager.getApplicationInfo(context.getPackageName(), 0);
            AppOpsManager appOpsManager = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);

            int mode = appOpsManager.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS, applicationInfo.uid, applicationInfo.packageName);

            retVal = (mode == AppOpsManager.MODE_ALLOWED) ? true : false;
        } catch (PackageManager.NameNotFoundException e)
        {
            return false;
        }

        return retVal;
    }
}

