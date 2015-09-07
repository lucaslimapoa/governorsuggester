package com.tcc.lucas.governorsuggestor;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.TrafficStats;

import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class SystemInformation {

    private Context mCurrentContext;
    private List<ApplicationInfo> mDeviceAppsList;
    private List<Application> mRankedAppsList;
    private ApplicationRanker mApplicationRanker;
    private float mTotalReceivedBytes;
    private float mTotalTransmittedBytes;

    public SystemInformation(Context context){
        mCurrentContext = context;
        mApplicationRanker = new ApplicationRanker();
        mTotalReceivedBytes = TrafficStats.getTotalRxBytes();
        mTotalTransmittedBytes = TrafficStats.getTotalTxBytes();
    }

    public List<ApplicationInfo> getDeviceAppsList(){
        return mDeviceAppsList;
    }

    public float getTotalReceivedBytes() {
        return mTotalReceivedBytes;
    }

    public float getTotalTransmittedBytes() {
        return mTotalTransmittedBytes;
    }

    public void setTotalReceivedBytes(float mTotalReceivedBytes) {
        this.mTotalReceivedBytes = mTotalReceivedBytes;
    }

    public void setTotalTransmittedBytes(float mTotalTransmittedBytes) {
        this.mTotalTransmittedBytes = mTotalTransmittedBytes;
    }

    public void collectSystemInformation(){
        mDeviceAppsList = mCurrentContext.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        mRankedAppsList = mApplicationRanker.rankApplication(mDeviceAppsList);
    }
}
