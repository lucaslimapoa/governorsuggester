package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Application {

    private String mName;
    private Definitions.ApplicationRank mRank;
    private float mBatteryUsed;
    private float mUsage;
    private float mRamUsed;
    private float mCpuUsed;
    private float mNetworkUsed;

    public Application( ) {
    }

    public Application( String mName, Definitions.ApplicationRank mRank, float mBatteryUsed, float mUsage ){
        this.mName = mName;
        this.mRank = mRank;
        this.mBatteryUsed = mBatteryUsed;
        this.mUsage = mUsage;
    }

    public String getName() {
        return mName;
    }


    public Definitions.ApplicationRank getRank() {
        return mRank;
    }

    public float getBatteryUsed() {
        return mBatteryUsed;
    }

    public float getUsage() {
        return mUsage;
    }

    public float getRamUsed() {
        return mRamUsed;
    }

    public float getCpuUsed() {
        return mCpuUsed;
    }

    public float getNetworkUsed() {
        return mNetworkUsed;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public void setRank(Definitions.ApplicationRank mRank) {
        this.mRank = mRank;
    }

    public void setBatteryUsed(float mBatteryUsed) {
        this.mBatteryUsed = mBatteryUsed;
    }

    public void setUsage(float mUsage) {
        this.mUsage = mUsage;
    }

    public void setRamUsed(float mRamUsed) {
        this.mRamUsed = mRamUsed;
    }

    public void setCpuUsed(float mCpuUsed) {
        this.mCpuUsed = mCpuUsed;
    }

    public void setNetworkUsed(float mNetworkUsed) {
        this.mNetworkUsed = mNetworkUsed;
    }
}
