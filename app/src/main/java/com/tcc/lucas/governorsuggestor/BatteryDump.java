package com.tcc.lucas.governorsuggestor;

import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/22/2015.
 */
public class BatteryDump extends AbstractDump
{
    // Command
    private String mCommand;

    // Member Variables
    private BatteryStats mBatteryStatsTemp;
    private Double mBatteryCapacity;
    private Long mRunTime;

    // Battery Usage Patterns
    private Pattern mEstimatePowerSectionRegex = Pattern.compile("(Estimated power use) \\(mAh\\):");
    private Pattern mBatteryCapacityRegex = Pattern.compile("(?!Capacity: )\\d+");
    private Pattern mRunTimeRegex = Pattern.compile("(Total run time: )(\\w+ ){1,5}realtime");
    private Pattern mUidPowerUsageRegex;

    // CPU Usage Patterns
    private Pattern mCPUInfoRegex = Pattern.compile("\\d\\w+");
    private Pattern mTimeUnitRegex = Pattern.compile("(?!\\d)\\w+");
    private Pattern mUniqueIdRegex = Pattern.compile("(u0\\w+):");
    private Pattern mProcessSectionRegex;

    // Constants
    private final String kHour = "h";
    private final String kMinute = "m";
    private final String kSecond = "s";
    private final String kMillisecond = "ms";

    private enum TimeUnit
    {
        HOUR,
        MINUTE,
        SECOND,
        MILLISECOND,
        UNKNOWN
    }

    public BatteryDump()
    {
        super();
    }

    @Override
    public void dump()
    {

    }

    public void dump(String packageName)
    {
        mBatteryStatsTemp = new BatteryStats();
        mCommand = "batterystats --charged " + packageName;

        List<String> outputReader = ProcessCommand.runRootCommand(createCommand(mCommand), false);

        if (outputReader != null)
        {
            mProcessSectionRegex = Pattern.compile("(Proc) " + packageName + ":(\\w+)?");

            if(getRunTime() == null)
                parseTotalRunTime(outputReader);

            parseCPUUsage(outputReader);

            if(mUidPowerUsageRegex != null)
                parseBatteryUsage(outputReader);

            if(mBatteryStatsTemp.isValid())
                mHashData.put(packageName, mBatteryStatsTemp);
        }
    }

    private void parseTotalRunTime(List<String> output)
    {
        boolean isStatisticsSection = false;

        for (String lineRead : output )
        {
            if(isStatisticsSection == false)
                isStatisticsSection = isBatteryStatisticsSection(lineRead);

            else
            {
                parseStatistics(lineRead);

                if(getRunTime() != null)
                    break;
            }
        }
    }

    private void parseStatistics(String info)
    {
        Matcher matcher = mRunTimeRegex.matcher(info);

        if(matcher.find())
        {
            String clean = matcher.group(0).replace("Total run time:", "");
            clean = clean.replace("realtime", "").trim();

            String[] times = clean.split(" ");

            long runTime = 0;
            for(int i = 0; i < times.length; i++)
            {
                runTime += parseTime(times[i]);
            }

            mRunTime = new Long(runTime);

        }
    }

    private void parseCPUUsage(List<String> output)
    {
        boolean processUsageSection = false;
        int procPosition = -1;

        ListIterator iterator = output.listIterator(0);

        String lineRead;
        while(iterator.hasNext())
        {
            lineRead = (String) iterator.next();

            if (processUsageSection == false)
                processUsageSection = parseProcessUsagePackage(lineRead);

            else
            {
                if(parseCPUInformation(lineRead))
                {
                    if(procPosition == -1)
                        procPosition = iterator.nextIndex();

                    processUsageSection = false;
                }
            }
        }

        parseUniqueId(output, procPosition);
    }

    private void parseUniqueId(List<String> output, int position)
    {
        String uniqueId = null;

        if(position > 0)
        {
            ListIterator iterator = output.listIterator(position);

            String lineRead;
            while(iterator.hasPrevious())
            {
                lineRead = (String) iterator.previous();

                if (uniqueId == null)
                    uniqueId = packageUniqueId(lineRead);

                else
                {
                    mUidPowerUsageRegex = Pattern.compile("((Uid "+ uniqueId + ") (\\d+.?\\d+))");
                    break;
                }
            }
        }
    }

    private void parseBatteryUsage(List<String> output)
    {
        boolean isEstimatePowerSection = false;

        for(String lineRead : output)
        {
            if(isEstimatePowerSection == false)
                isEstimatePowerSection = isEstimatePowerSection(lineRead);

            else
            {
                if (mBatteryCapacity == null)
                    mBatteryCapacity = parseBatteryCapacity(lineRead);

                else
                {
                    if (isUidPowerUsage(lineRead))
                    {
                        double batterySpent = parsePowerUsage(lineRead);
                        mBatteryStatsTemp.setBatteryUsed(batterySpent);

                        break;
                    }
                }
            }
        }
    }

    private boolean parseProcessUsagePackage(String info)
    {
        boolean isProcessUsageSection = false;

        Matcher match = mProcessSectionRegex.matcher(info);

        if (match.find())
            isProcessUsageSection = true;

        return isProcessUsageSection;
    }

    private boolean parseCPUInformation(String info)
    {
        boolean retVal = false;

        Matcher matcher = mCPUInfoRegex.matcher(info);

        if (matcher.find())
        {
            info = info.replace("CPU:", "").trim();
            String[] split = info.split(";");

            if (split.length == 2)
            {
                String[] usrAndKrn = split[0].split("\\+");

                if (usrAndKrn.length == 2)
                {
                    double cpuUser = parseTime(usrAndKrn[0]);
                    mBatteryStatsTemp.setCPUUser(mBatteryStatsTemp.getCPUUser() + cpuUser);

                    double cpuKernel = parseTime(usrAndKrn[1]);
                    mBatteryStatsTemp.setCPUKernel(mBatteryStatsTemp.getCPUKernel() + cpuKernel);

                    double cpuForeground = parseTime(split[1]);
                    mBatteryStatsTemp.setCPUForeground(mBatteryStatsTemp.getCPUForeground() + cpuForeground);

                    double cpuPercent = (100 * mBatteryStatsTemp.getTotalCPUTime()) / getRunTime();
                    mBatteryStatsTemp.setCPUPercent(cpuPercent);

                    retVal = true;
                }
            }
        }

        return retVal;
    }

    private double parseTime(String usr)
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
                        retVal += ((Double.parseDouble(time) * 3600) * 1000);
                        break;

                    case MINUTE:
                        time = time.replace(kMinute, "");
                        retVal += ((Double.parseDouble(time) * 60) * 1000);
                        break;

                    case SECOND:
                        time = time.replace(kSecond, "");
                        retVal += (Double.parseDouble(time) * 1000);
                        break;
                    case MILLISECOND:
                        time = time.replace(kMillisecond, "");
                        retVal += Double.parseDouble(time);
                        break;
                }
            }
        }

        return retVal;
    }

    private TimeUnit parseTimeUnit(String text)
    {
        TimeUnit retVal = TimeUnit.UNKNOWN;

        Matcher matcher = mTimeUnitRegex.matcher(text);

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

    private String packageUniqueId(String info)
    {
        String retVal = null;

        Matcher matcher = mUniqueIdRegex.matcher(info);

        if(matcher.find())
        {
            if(matcher.group(0).equals("1000:") == false)
                retVal = matcher.group(0);
        }

        return retVal;
    }

    private Double parsePowerUsage(String info)
    {
        Double estimatedPowerUsage = null;

        String[] split = info.split(":");

        if (split.length == 2)
            estimatedPowerUsage = (100 * Double.parseDouble(split[1].trim())) / mBatteryCapacity;

        return estimatedPowerUsage;
    }

    private Double parseBatteryCapacity(String info)
    {
        Double retVal = null;

        Matcher matcher = mBatteryCapacityRegex.matcher(info);

        if (matcher.find())
        {
            String[] split = matcher.group(0).split(" ");

            if (split.length > 0)
                retVal = Double.parseDouble(split[0]);
        }

        return retVal;
    }

    private boolean isBatteryStatisticsSection(String info)
    {
        boolean isBatteryStatisticsSection = false;

        if(info.matches("Statistics since last charge:"))
            isBatteryStatisticsSection = true;

        return isBatteryStatisticsSection;
    }

    private boolean isEstimatePowerSection(String info)
    {
        boolean retVal = false;

        Matcher matcher = mEstimatePowerSectionRegex.matcher(info);

        if(matcher.find())
            retVal = true;

        return retVal;
    }

    private boolean isUidPowerUsage(String info)
    {
        boolean retVal = false;

        Matcher matcher = mUidPowerUsageRegex.matcher(info);

        if (matcher.find())
            retVal = true;

        return retVal;
    }

    public Long getRunTime()
    {
        return mRunTime;
    }
}