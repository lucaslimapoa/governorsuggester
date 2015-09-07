package com.tcc.lucas.governorsuggestor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class SystemInformation {

    private Context mCurrentContext;
    private List<ApplicationInfo> mDeviceAppsList;
    private ApplicationRanker mApplicationRanker;

    public SystemInformation(Context context){
        mCurrentContext = context;
        mApplicationRanker = new ApplicationRanker();
    }

    public List<ApplicationInfo> getDeviceAppsList(){
        return mDeviceAppsList;
    }

    public void collectSystemInformation(){
        mDeviceAppsList = mCurrentContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);

        mApplicationRanker.rankApplication(mDeviceAppsList);

    }
}
