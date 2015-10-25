package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/23/2015.
 */
public class ProcessDump extends AbstractDump
{
    private double kInvalidTotalTime = -1;
    // Meminfo separators
    public final String PROC = "proc";
    public final String SERVICE_A = "servicea";
    public final String SERVICE_B = "serviceb";
    public final String CACHED = "cached";

    private final String COMMAND = "procstats --hours 24";

    // Member variables
    private HashMap<String, ProcStats> mProcessDump;
    private BufferedReader mOutputReader;

    // v1 = com([\.*]\w+)+
    // v2 = com([\.*]\w+)+:\w+
    private Pattern mPackageNameRegex = Pattern.compile("com([\\.*]\\w+)+(:\\w+)?");
    private Pattern mTotalRegex = Pattern.compile("(TOTAL:) \\w+?.\\w");
    private Pattern mMemoryRegex = Pattern.compile("(\\d.?\\d(?=MB)).*MB");

    public ProcessDump()
    {
        mProcessDump = new HashMap<>();
        mOutputReader = ProcessCommand.runRootCommand(createCommand(), true);

        try
        {
            if(mOutputReader != null)
                dump();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected String[] createCommand()
    {
        String[] command = {DUMPSYS + COMMAND};
        return command;
    }

    @Override
    protected void dump() throws IOException
    {
        String lineRead;

        ProcStats process = null;

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            String packageName = getPackageName(lineRead);

            if(packageName != null)
            {
                process = new ProcStats();
                process.setName(packageName);
            }

            else if(process != null)
            {
                double totalRunTime = getProcessTotalTime(lineRead);

                if(totalRunTime != kInvalidTotalTime)
                {
                    process.setTotalTime(totalRunTime);

                    MemoryStats memoryStats = getMemoryStats(lineRead);

                    if(memoryStats != null)
                    {
                        process.setMemoryStats(memoryStats);
                        mProcessDump.put(process.getName(), process);

                        process = null;
                    }
                }
            }
        }
    }

    private String getPackageName(String info)
    {
        String matchedString = null;

        Matcher matcher = mPackageNameRegex.matcher(info);

        if(matcher.find())
            matchedString = matcher.group(0);

        return matchedString;
    }

    //TOTAL: 100% (6.1MB-11MB-14MB/5.4MB-9.8MB-13MB over 138)
    private double getProcessTotalTime(String info)
    {
        double retVal = kInvalidTotalTime;

        Matcher matcher = mTotalRegex.matcher(info);

        if(matcher.find())
        {
            String[] split = matcher.group(0).split(":");
            if(split.length > 1)
            {
                String temp = split[split.length - 1];
                temp = temp.replace(" ", "");
                retVal = Double.parseDouble(temp);
            }
        }

        return retVal;
    }

    private MemoryStats getMemoryStats(String info)
    {
        MemoryStats memoryStats = null;

        info = info.replace("-", " ");
        Matcher matcher = mMemoryRegex.matcher(info);

        if(matcher.find())
        {
            String match = matcher.group(0).replace("MB", " ");
            match = match.replace("/", " ");
            String[] split = match.split(" ");

            if(split.length >= 4)
            {
                memoryStats = new MemoryStats();

                //minPss-avgPss-maxPss / minUss-avgUss-maxUss
                memoryStats.setMinPss(Double.parseDouble(split[0]) * 1024);
                memoryStats.setAvgPss(Double.parseDouble(split[2]) * 1024);
                memoryStats.setMaxPss(Double.parseDouble(split[4]) * 1024);
                memoryStats.setMinUss(Double.parseDouble(split[6]) * 1024);
                memoryStats.setAvgUss(Double.parseDouble(split[8]) * 1024);
                memoryStats.setMaxUss(Double.parseDouble(split[10]) * 1024);
            }
        }

        return memoryStats;
    }
}

class MemoryStats
{
    private double mMaxPss;
    private double mAvgPss;
    private double mMinPss;

    private double mMaxUss;
    private double mAvgUss;
    private double mMinUss;

    public MemoryStats()
    {

    }

    public double getMaxPss()
    {
        return mMaxPss;
    }

    public void setMaxPss(double mMaxPss)
    {
        this.mMaxPss = mMaxPss;
    }

    public double getAvgPss()
    {
        return mAvgPss;
    }

    public void setAvgPss(double mAvgPss)
    {
        this.mAvgPss = mAvgPss;
    }

    public double getMinPss()
    {
        return mMinPss;
    }

    public void setMinPss(double mMinPss)
    {
        this.mMinPss = mMinPss;
    }

    public double getMaxUss()
    {
        return mMaxUss;
    }

    public void setMaxUss(double mMaxUss)
    {
        this.mMaxUss = mMaxUss;
    }

    public double getAvgUss()
    {
        return mAvgUss;
    }

    public void setAvgUss(double mAvgUss)
    {
        this.mAvgUss = mAvgUss;
    }

    public double getMinUss()
    {
        return mMinUss;
    }

    public void setMinUss(double mMinUss)
    {
        this.mMinUss = mMinUss;
    }
}

class ProcStats
{
    private String mName;
    private double mTotalTime;
    private MemoryStats mMemoryStats;

    public ProcStats()
    {

    }

    public String getName()
    {
        return mName;
    }

    public void setName(String mName)
    {
        this.mName = mName;
    }

    public double getTotalTime()
    {
        return mTotalTime;
    }

    public void setTotalTime(double mTotalTime)
    {
        this.mTotalTime = mTotalTime;
    }

    public MemoryStats getMemoryStats()
    {
        return mMemoryStats;
    }

    public void setMemoryStats(MemoryStats mMemoryStats)
    {
        this.mMemoryStats = mMemoryStats;
    }
}
