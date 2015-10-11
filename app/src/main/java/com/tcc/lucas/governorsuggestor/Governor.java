package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/11/2015.
 */
public class Governor
{
    private final String LOG_TAG = getClass().getSimpleName();

    private Definitions.Governor mName;
    private float mUXMultitask;
    private float mUXRuntime;
    private float mCPUInteger;
    private float mCPUFloat;
    private float mCPUSingleThreadInteger;
    private float mCPUSingleThreadFloat;
    private float mRAMOperation;
    private float mRAMSpeed;
    private float mGPU2D;
    private float mGPU3D;
    private float mIOStorage;
    private float mIODatabase;
    private float mBattery;

    private float mUXOverall;
    private float mCPUOverall;
    private float mRAMOverall;
    private float mGPUOverall;
    private float mIOOverall;

    public Governor(Definitions.Governor name, float uxMultitask, float uxRuntime, float cpuInt,
                    float cpuFloat, float cpuSTInt, float cpuSTFloat, float ramOp, float ramSpeed,
                    float gpu2D, float gpu3D, float ioStore, float ioData, float battery)
    {
        this.mName = name;
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
        this.mBattery = battery;

        this.setUXOverall(uxMultitask + uxRuntime);
        this.setCPUOverall(cpuInt + cpuFloat + cpuSTInt + cpuSTFloat);
        this.setRAMOverall(ramOp + ramSpeed);
        this.setGPUOverall(gpu2D + gpu3D);
        this.setIOOverall(ioStore + ioData);
    }

    public float getUXOverall()
    {
        return mUXOverall;
    }

    public void setUXOverall(float mUXOverall)
    {
        this.mUXOverall = mUXOverall;
    }

    public float getCPUOverall()
    {
        return mCPUOverall;
    }

    public void setCPUOverall(float mCPUOverall)
    {
        this.mCPUOverall = mCPUOverall;
    }

    public float getRAMOverall()
    {
        return mRAMOverall;
    }

    public void setRAMOverall(float mRAMOverall)
    {
        this.mRAMOverall = mRAMOverall;
    }

    public float getGPUOverall()
    {
        return mGPUOverall;
    }

    public void setGPUOverall(float mGPUOverall)
    {
        this.mGPUOverall = mGPUOverall;
    }

    public float getIOOverall()
    {
        return mIOOverall;
    }

    public void setIOOverall(float mIOOverall)
    {
        this.mIOOverall = mIOOverall;
    }
}
