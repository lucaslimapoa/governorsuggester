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
    private ListView mGovernorListView;
    private TextView mMinCPUFrequency;
    private TextView mMaxCPUFrequency;
    private TextView mCurrentCPUFrequency;
    private TextView mCurrentGovernor;

    // Logic Variables
    private SystemInformation mSystemInformation;
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

        mSystemInformation.execute();
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
                GovernorArrayAdapter governorArrayAdapter = new GovernorArrayAdapter(getApplicationContext(), governorList);

                mGovernorListView.setAdapter(governorArrayAdapter);
            }
        };

        mUIHandler = new Handler();
    }

    private void initUI()
    {
        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mGovernorListView = (ListView) findViewById(R.id.governorListView);

        mMinCPUFrequency = (TextView) findViewById(R.id.cpuMinimumFrequencyValueTextView);
        mMinCPUFrequency.setText(mSystemInformation.getCPUInformation().getMinCPUFreq());

        mMaxCPUFrequency = (TextView) findViewById(R.id.cpuMaximumFrequencyValueTextView);
        mMaxCPUFrequency.setText(mSystemInformation.getCPUInformation().getMaxCPUFreq());

        mCurrentCPUFrequency = (TextView) findViewById(R.id.cpuCurrentFrequencyTextView);
        mCurrentCPUFrequency.setText(mSystemInformation.getCPUInformation().getCurrentCPUFreq());

        mCurrentGovernor = (TextView) findViewById(R.id.governorValueTextView);
        mCurrentGovernor.setText(mSystemInformation.getCurrentGovernor());
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
                mUIHandler.postDelayed(this, mIntervalRate);
            }
        }, mIntervalRate);
    }
}
