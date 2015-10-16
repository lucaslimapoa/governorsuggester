package com.tcc.lucas.governorsuggestor;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    // UI Elements
    private Toolbar mToolbar;
    private TextView mDeviceTextView;
    private ListView mGovernorListView;

    // Logic Variables
    private UserProfile mUserProfile;
    private int mIntervalRate = 1500;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDeviceTextView = (TextView) findViewById(R.id.deviceTextView);
        mGovernorListView = (ListView) findViewById(R.id.governorListView);
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

        mUserProfile = new UserProfile(getApplicationContext())
        {
            @Override
            protected void onPostExecute(List<Application> applicationList)
            {
                super.onPostExecute(applicationList);

                List<Governor> governorList = mUserProfile.getSystemInformation()
                        .getApplicationRanker().getGovernorList();

                GovernorArrayAdapter governorArrayAdapter = new GovernorArrayAdapter(getApplicationContext(), governorList);

                mGovernorListView.setAdapter(governorArrayAdapter);
            }
        };

        mUserProfile.execute();

//        mDeviceTextView.setText(mUserProfile.getSystemInformation().getCPUInformation().getCurrentCPUFreq());

        final Handler cpuFrequencyHandler = new Handler();
        cpuFrequencyHandler.postDelayed(new Runnable()
        {
            @Override
            public void run()
            {
                String currentCPUFreq = mUserProfile.getSystemInformation().getCPUInformation().getCurrentCPUFreq();
                mDeviceTextView.setText(currentCPUFreq);

                cpuFrequencyHandler.postDelayed(this, mIntervalRate);
            }
        }, mIntervalRate);

        //mUserProfile.getSystemInformation().DeviceModel);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
