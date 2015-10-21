package com.tcc.lucas.governorsuggestor;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

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

        mGovernorListView.setVisibility(View.INVISIBLE);
        mListViewProgressBar.setVisibility(View.VISIBLE);
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
                mGovernorArrayAdapter = new GovernorArrayAdapter(getApplicationContext(), governorList);

                mListViewProgressBar.setVisibility(View.INVISIBLE);
                mGovernorListView.setAdapter(mGovernorArrayAdapter );
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
}

