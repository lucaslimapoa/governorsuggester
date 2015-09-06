package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Application {

    private String mName;
    private Definitions.ApplicationRank mRank;
    private float mBatteryUsed;
    private float mUsage;

    public Application( String mName, Definitions.ApplicationRank mRank, float mBatteryUsed, float mUsage ){
        this.mName = mName;
        this.mRank = mRank;
        this.mBatteryUsed = mBatteryUsed;
        this.mUsage = mUsage;
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public Definitions.ApplicationRank getRank() {
        return mRank;
    }

    public void setmRank(Definitions.ApplicationRank mRank) {
        this.mRank = mRank;
    }

    public float getBatteryUsed() {
        return mBatteryUsed;
    }

    public void setBatteryUsed(float mBatteryUsed) {
        this.mBatteryUsed = mBatteryUsed;
    }

    public float getUsage() {
        return mUsage;
    }

    public void setUsage(float mUsage) {
        this.mUsage = mUsage;
    }
}
