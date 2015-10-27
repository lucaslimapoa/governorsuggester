package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/23/2015.
 */
public class ProcessDump extends AbstractDump
{
    private double kInvalidTotalTime = -1;
    private final String COMMAND = "procstats --hours 24";

    // Member variables
    private BufferedReader mOutputReader;

    // v1 = com([\.*]\w+)+
    // v2 = com([\.*]\w+)+:\w+
    private Pattern mPackageNameRegex = Pattern.compile("com([\\.*]\\w+)+(:\\w+)? / \\w+");
    private Pattern mTotalRegex = Pattern.compile("(TOTAL:) \\w+?.\\w");
    private Pattern mMemoryRegex = Pattern.compile("(\\d.?\\d(?=MB)).*MB");

    public ProcessDump()
    {
        super();

        mOutputReader = ProcessCommand.runRootCommand(createCommand(), true);

        if(mOutputReader != null)
            dump();
    }

    @Override
    protected String[] createCommand()
    {
        String[] command = {DUMPSYS + COMMAND};
        return command;
    }

    @Override
    protected void dump()
    {
        try
        {
            collectProcessStatistics();
            collectBatteryStatistics();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void collectProcessStatistics() throws IOException
    {
        String lineRead;

        ProcessStats process = null;

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            String packageName = parsePackageName(lineRead);

            if(packageName != null)
            {
                String[] split = packageName.split("/");

                if(split.length == 2)
                {
                    process = new ProcessStats();

                    process.setPackageName(split[0].trim());
                    process.setUid(split[1].trim());
                }
            }

            else if(process != null)
            {
                double totalRunTime = parseProcessTotalTime(lineRead);

                if(totalRunTime != kInvalidTotalTime)
                {
                    process.setTotalTime(totalRunTime);

                    MemoryStats memoryStats = parseMemoryStats(lineRead);

                    if(memoryStats != null)
                    {
                        process.setMemoryStats(memoryStats);
                        mHashData.put(process.getPackageName(), process);

                        process = null;
                    }
                }
            }
        }
    }

    private void collectBatteryStatistics()
    {
        BatteryDump batteryDump = new BatteryDump();

        for (String key : mHashData.keySet())
        {
            ProcessStats processStats = (ProcessStats) get(key);
            Double consumption = (Double) batteryDump.get(processStats.getUid());

            if(consumption != null)
            {
                processStats.setBatteryUsage(consumption);
                mHashData.put(key, processStats);
            }
        }
    }

    private String parsePackageName(String info)
    {
        String matchedString = null;

        Matcher matcher = mPackageNameRegex.matcher(info);

        if(matcher.find())
            matchedString = matcher.group(0);

        return matchedString;
    }

    //TOTAL: 100% (6.1MB-11MB-14MB/5.4MB-9.8MB-13MB over 138)
    private double parseProcessTotalTime(String info)
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

    private MemoryStats parseMemoryStats(String info)
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