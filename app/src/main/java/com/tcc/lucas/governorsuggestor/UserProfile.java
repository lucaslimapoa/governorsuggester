package com.tcc.lucas.governorsuggestor;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class UserProfile extends AsyncTask<Void, Void, List<Application>> {

    private Definitions.Governor mBestProfileGovernor;
    private SystemInformation mSystemInformation;

    public UserProfile(Context context){
        mSystemInformation = new SystemInformation(context);
    }

    @Override
    protected List<Application> doInBackground(Void... params) {
        mSystemInformation.collectSystemInformation();
        return null;
    }
}
