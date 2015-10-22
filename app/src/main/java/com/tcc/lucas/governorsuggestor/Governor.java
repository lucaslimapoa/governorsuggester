package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/11/2015.
 */
public class Governor
{
    private final String LOG_TAG = getClass().getSimpleName();

    private String mDevice;
    private Definitions.Governor mName;
    private double mUXMultitask;
    private double mUXRuntime;
    private double mCPUInteger;
    private double mCPUFloat;
    private double mCPUSingleThreadInteger;
    private double mCPUSingleThreadFloat;
    private double mRAMOperation;
    private double mRAMSpeed;
    private double mGPU2D;
    private double mGPU3D;
    private double mIOStorage;
    private double mIODatabase;

    private double mUXOverall;
    private double mCPUOverall;
    private double mRAMOverall;
    private double mGPUOverall;
    private double mIOOverall;
    private double mBatteryOverall;

    private double mTotalScore;

    public Governor(String device, Definitions.Governor name, double uxMultitask, double uxRuntime, double cpuInt,
                    double cpuFloat, double cpuSTInt, double cpuSTFloat, double ramOp, double ramSpeed,
                    double gpu2D, double gpu3D, double ioStore, double ioData, double battery,
                    double uxOverall, double cpuOverall, double ramOverall, double gpuOverall,
                    double ioOverall)
    {
        this.setDevice(device);
        this.setName(name);

        this.mUXMultitask = uxMultitask;
        this.mUXRuntime = uxRuntime;
        this.mCPUInteger = cpuInt;
        this.mCPUFloat = cpuFloat;
        this.mCPUSingleThreadInteger = cpuSTInt;
        this.mCPUSingleThreadFloat = cpuSTFloat;
        this.mRAMOperation = ramOp;
        this.mRAMSpeed = ramSpeed;
        this.mGPU2D = gpu2D;
        this.mGPU3D = gpu3D;
        this.mIOStorage = ioStore;
        this.mIODatabase = ioData;

        this.mUXOverall  = uxOverall;
        this.mCPUOverall = cpuOverall;
        this.mRAMOverall = ramOverall;
        this.mGPUOverall = gpuOverall;
        this.mIOOverall  = ioOverall;
        this.mBatteryOverall = battery;
    }

    public double getUXOverall()
    {
        return mUXOverall;
    }

    public void setUXOverall(double mUXOverall)
    {
        this.mUXOverall = mUXOverall;
    }

    public double getCPUOverall()
    {
        return mCPUOverall;
    }

    public void setCPUOverall(double mCPUOverall)
    {
        this.mCPUOverall = mCPUOverall;
    }

    public double getRAMOverall()
    {
        return mRAMOverall;
    }

    public void setRAMOverall(double mRAMOverall)
    {
        this.mRAMOverall = mRAMOverall;
    }

    public double getGPUOverall()
    {
        return mGPUOverall;
    }

    public void setGPUOverall(double mGPUOverall)
    {
        this.mGPUOverall = mGPUOverall;
    }

    public double getIOOverall()
    {
        return mIOOverall;
    }

    public void setIOOverall(double mIOOverall)
    {
        this.mIOOverall = mIOOverall;
    }

    public double getBatteryOverall()
    {
        return mBatteryOverall;
    }

    public Definitions.Governor getName()
    {
        return mName;
    }

    public void setName(Definitions.Governor mName)
    {
        this.mName = mName;
    }

    public double getTotalScore()
    {
        return mTotalScore;
    }

    public void setTotalScore(double mTotalScore)
    {
        this.mTotalScore = mTotalScore;
    }

    public String getDevice()
    {
        return mDevice;
    }

    public void setDevice(String mDevice)
    {
        this.mDevice = mDevice;
    }
}
