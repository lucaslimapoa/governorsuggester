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
    private final String COMMAND = "batterystats --charged ";

    // Member Variables
    private Double mBatteryCapacity;
    private Double mBatterySpent;
    private BatteryStats mBatteryStatsTemp;

    // Battery Usage Patterns
    private Pattern mEstimatePowerSectionPattern = Pattern.compile("(Estimated power use) \\(mAh\\):");
    private Pattern mBatteryCapacityPattern = Pattern.compile("(?!Capacity: )\\d+");
    private Pattern mUidPowerUsagePattern;

    // CPU Usage Patterns
    private Pattern mCPUInfoPattern = Pattern.compile("\\d\\w+");
    private Pattern mTimeUnitPattern = Pattern.compile("(?!\\d)\\w+");
    private Pattern mUniqueIdPattern = Pattern.compile("(u0\\w+):");
    private Pattern mProcessSectionPattern;

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

    protected String[] createCommand(String parameter)
    {
        String[] command = {DUMPSYS + COMMAND + parameter};
        return command;
    }

    @Override
    protected String[] createCommand()
    {
        return null;
    }

    @Override
    public void dump()
    {

    }

    public void dump(String packageName)
    {
        mBatteryStatsTemp = new BatteryStats();
        List<String> outputReader = ProcessCommand.runRootCommand(createCommand(packageName), false);

        if (outputReader != null)
        {
            mProcessSectionPattern = Pattern.compile("(Proc) " + packageName + ":(\\w+)?");

            parseCPUUsage(outputReader);
            parseBatteryUsage(outputReader);

            if(mBatteryStatsTemp.isValid())
                mHashData.put(packageName, mBatteryStatsTemp);
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
                    mUidPowerUsagePattern = Pattern.compile("((Uid "+ uniqueId + ") (\\d+.?\\d+))");
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
                        mBatterySpent = parsePowerUsage(lineRead);
                        break;
                    }
                }
            }
        }
    }

    private boolean parseProcessUsagePackage(String info)
    {
        boolean isProcessUsageSection = false;

        Matcher match = mProcessSectionPattern.matcher(info);

        if (match.find())
            isProcessUsageSection = true;

        return isProcessUsageSection;
    }

    private boolean parseCPUInformation(String info)
    {
        boolean retVal = false;

        Matcher matcher = mCPUInfoPattern.matcher(info);

        if (matcher.find())
        {
            info = info.replace("CPU:", "").trim();
            String[] split = info.split(";");

            if (split.length == 2)
            {
                String[] usrAndKrn = split[0].split("\\+");

                if (usrAndKrn.length == 2)
                {
                    double cpuUser = parseCPUTime(usrAndKrn[0]);
                    mBatteryStatsTemp.setCPUUser(mBatteryStatsTemp.getCPUUser() + cpuUser);

                    double cpuKernel = parseCPUTime(usrAndKrn[1]);
                    mBatteryStatsTemp.setCPUKernel(mBatteryStatsTemp.getCPUKernel() + cpuKernel);

                    double cpuForeground = parseCPUTime(split[1]);
                    mBatteryStatsTemp.setCPUForeground(mBatteryStatsTemp.getCPUForeground() + cpuForeground);

                    retVal = true;
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

    private String packageUniqueId(String info)
    {
        String retVal = null;

        Matcher matcher = mUniqueIdPattern.matcher(info);

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

        Matcher matcher = mBatteryCapacityPattern.matcher(info);

        if (matcher.find())
        {
            String[] split = matcher.group(0).split(" ");

            if (split.length > 0)
                retVal = Double.parseDouble(split[0]);
        }

        return retVal;
    }

    private boolean isEstimatePowerSection(String info)
    {
        boolean retVal = false;

        Matcher matcher = mEstimatePowerSectionPattern.matcher(info);

        if(matcher.find())
            retVal = true;

        return retVal;
    }

    private boolean isUidPowerUsage(String info)
    {
        boolean retVal = false;

        Matcher matcher = mUidPowerUsagePattern.matcher(info);

        if (matcher.find())
            retVal = true;

        return retVal;
    }
}