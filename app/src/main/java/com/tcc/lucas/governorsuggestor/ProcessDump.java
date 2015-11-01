package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/23/2015.
 */
public class ProcessDump extends AbstractDump
{
    private double kInvalidTotalTime = -1;
    private String mCommand;

    // Member variables
    private List<String> mOutputReader;

    // v1 = com([\.*]\w+)+
    // v2 = com([\.*]\w+)+:\w+
    private Pattern mPackageNameRegex = Pattern.compile("com([\\.*]\\w+)+(:\\w+)? / \\w+");
    private Pattern mTotalRegex = Pattern.compile("(TOTAL:) \\w+?.\\w");
    private Pattern mMemoryRegex = Pattern.compile("(\\d.?\\d(?=MB)).*MB");

    public ProcessDump(long time)
    {
        super();

        mCommand = "procstats --hours " + Long.toString(time);
        mOutputReader = ProcessCommand.runRootCommand(createCommand(mCommand), true);

        if(mOutputReader != null)
            dump();
    }

    @Override
    protected void dump()
    {
        try
        {
            collectProcessStatistics();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void collectProcessStatistics() throws IOException
    {
        ProcessStats process = null;

        for(String lineRead : mOutputReader)
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
                memoryStats.setMinPss(Double.parseDouble(split[0]));
                memoryStats.setAvgPss(Double.parseDouble(split[2]));
                memoryStats.setMaxPss(Double.parseDouble(split[4]));
                memoryStats.setMinUss(Double.parseDouble(split[6]));
                memoryStats.setAvgUss(Double.parseDouble(split[8]));
                memoryStats.setMaxUss(Double.parseDouble(split[10]));
            }
        }

        return memoryStats;
    }
}