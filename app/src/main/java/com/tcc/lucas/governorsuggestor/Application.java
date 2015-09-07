package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Application {

    private String mName;
    private Definitions.ApplicationRank mRank;
    private float mBatteryUsed;
    private float mRunTime;
    private float mRamUsed;
    private float mCpuUsed;
    private float mBytesSent;
    private float mBytesReceived;

    public Application( ) {
    }

    public Application( String mName, Definitions.ApplicationRank mRank, float mBatteryUsed, float mUsage ){
        this.mName = mName;
        this.mRank = mRank;
        this.mBatteryUsed = mBatteryUsed;
        this.mRunTime = mUsage;
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

    public float getRunTime() {
        return mRunTime;
    }

    public float getRamUsed() {
        return mRamUsed;
    }

    public float getCpuUsed() {
        return mCpuUsed;
    }

    public float getBytesSent() {
        return mBytesSent;
    }

    public float getBytesReceived() {
        return mBytesReceived;
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

    public void setRunTime(float mUsage) {
        this.mRunTime = mUsage;
    }

    public void setRamUsed(float mRamUsed) {
        this.mRamUsed = mRamUsed;
    }

    public void setCpuUsed(float mCpuUsed) {
        this.mCpuUsed = mCpuUsed;
    }

    public void setBytesSent(float mBytesSent) {
        this.mBytesSent = mBytesSent;
    }

    public void setBytesReceived(float mBytesReceived) {
        this.mBytesReceived = mBytesReceived;
    }
}