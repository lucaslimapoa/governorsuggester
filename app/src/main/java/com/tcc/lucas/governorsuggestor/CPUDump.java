package com.tcc.lucas.governorsuggestor;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/28/2015.
 */
public class CPUDump extends AbstractDump
{
    private final String COMMAND = "batterystats";

    private final String kHour = "h";
    private final String kMinute = "m";
    private final String kSecond = "s";
    private final String kMillisecond = "ms";

    // Member variables
    private List<String> mOutputReader;
    private double mTotalCPUTime;
    private Pattern mProcessSectionPattern = Pattern.compile("(Proc) com([\\.*]\\w+)+(:\\w+)?\\w+:");
    private Pattern mCPUInfoPattern = Pattern.compile("\\d\\w+");
    private Pattern mTimeUnitPattern = Pattern.compile("(?!\\d)\\w+");

    private enum TimeUnit
    {
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND,
        UNKNOWN
    }

    public CPUDump()
    {
        super();

        mOutputReader = ProcessCommand.runRootCommand(createCommand(), false);

        if (mOutputReader != null)
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
            parseProcessCPUSection();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void parseProcessCPUSection() throws IOException
    {
        String packageName = null;
        CPUStats CPUStats;

        for(String lineRead : mOutputReader)
        {
            if (packageName == null)
                packageName = parseProcessUsagePackage(lineRead);

            else
            {
                CPUStats = parseCPUInformation(lineRead);

                if(CPUStats != null)
                {
                    mHashData.put(packageName, CPUStats);

                    mTotalCPUTime += CPUStats.getCPUUser() +
                            CPUStats.getCPUKernel() +
                            CPUStats.getCPUForeground();
                }

                packageName = null;
            }
        }
    }

    private String parseProcessUsagePackage(String info)
    {
        String packageName = null;

        Matcher match = mProcessSectionPattern.matcher(info);

        if (match.find())
        {
            String packageTemp = match.group(0);
            packageName = packageTemp.replace("Proc", "").trim();
        }

        return packageName;
    }

    private CPUStats parseCPUInformation(String info)
    {
        CPUStats retVal = null;

        Matcher matcher = mCPUInfoPattern.matcher(info);

        if (matcher.find())
        {
            // CPU: 160ms usr + ; 0ms fg
            info = info.replace("CPU:", "").trim();

            String[] split = info.split(";");

            if (split.length == 2)
            {
                String[] usrAndKrn = split[0].split("\\+");

                if (usrAndKrn.length == 2)
                {
                    retVal = new CPUStats();

                    double cpuUser = parseCPUTime(usrAndKrn[0]);
                    retVal.setCPUUser(cpuUser);

                    double cpuKernel = parseCPUTime(usrAndKrn[1]);
                    retVal.setCPUKernel(cpuKernel);

                    double cpuForeground = parseCPUTime(split[1]);
                    retVal.setCPUForeground(cpuForeground);
                }
            }
        }

        return retVal;
    }

    private double parseCPUTime(String usr)
    {
        double retVal = 0;

        String[] split = usr.split(" ");

        if (split.length > 0)
        {
            for (String time : split)
            {
                TimeUnit timeUnit = parseTimeUnit(time);

                switch (timeUnit)
                {
                    case HOUR:
                        time = time.replace(kHour, "");
                        retVal += (Double.parseDouble(time) * 3600);
                        break;

                    case MINUTE:
                        time = time.replace(kMinute, "");
                        retVal += (Double.parseDouble(time) * 60);
                        break;

                    case SECOND:
                        time = time.replace(kSecond, "");
                        retVal += Double.parseDouble(time);
                        break;
                    case MILLISECOND:
                        time = time.replace(kMillisecond, "");
                        retVal += (Double.parseDouble(time) / 1000);
                        break;
                }
            }
        }

        return retVal;
    }

    private TimeUnit parseTimeUnit(String text)
    {
        TimeUnit retVal = TimeUnit.UNKNOWN;

        Matcher matcher = mTimeUnitPattern.matcher(text);

        if(matcher.find())
        {
            if(matcher.group(0).equalsIgnoreCase(kHour))
                retVal = TimeUnit.HOUR;

            else if(matcher.group(0).equalsIgnoreCase(kMinute))
                retVal = TimeUnit.MINUTE;

            else if(matcher.group(0).equalsIgnoreCase(kSecond))
                retVal = TimeUnit.SECOND;

            else if(matcher.group(0).equalsIgnoreCase(kMillisecond))
                retVal = TimeUnit.MILLISECOND;
        }

        return retVal;
    }
}
