package com.tcc.lucas.governorsuggestor;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import java.util.List;

public class MainActivity extends AppCompatActivity
{
    // UI Elements
    private Toolbar mToolbar;
    private TextView mDeviceTextView;

    // Logic Variables
    private UserProfile mUserProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.app_bar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        mDeviceTextView = (TextView) findViewById(R.id.deviceTextView);
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

            }
        };

        mUserProfile.execute();

        mDeviceTextView.setText(mUserProfile.getSystemInformation().DeviceModel);
    }

    @Override
    protected void onResume()
    {
        super.onResume();
    }
}
