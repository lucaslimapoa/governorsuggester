package com.tcc.lucas.governorsuggestor;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/22/2015.
 */
public class BatteryDump extends AbstractDump
{
    private final String COMMAND = "batterystats --charged ";

    // Member variables
    private List<String> mOutputReader;
    private String mPackageName;
    private Double mBatteryCapacity;

    private HashMap<String, Object> mTempCPUTimeMap;

    // Battery Usage Patterns
    private Pattern mEstimatePowerSectionPattern = Pattern.compile("(Estimated power use) \\(mAh\\):");
    private Pattern mUidPowerUsagePattern = Pattern.compile("(Uid) \\w+: ?\\w+.?\\w+");
    private Pattern mBatteryCapacityPattern = Pattern.compile("(?!Capacity: )\\d+");

    // CPU Usage Patterns
    private Pattern mProcessSectionPattern = Pattern.compile("(Proc) com([\\.*]\\w+)+(:\\w+)?\\w+:");
    private Pattern mCPUInfoPattern = Pattern.compile("\\d\\w+");
    private Pattern mTimeUnitPattern = Pattern.compile("(?!\\d)\\w+");
    private Pattern mUniqueIdPattern = Pattern.compile("(u0\\w+):");

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

    public BatteryDump(String packageName)
    {
        super();

        this.mPackageName = packageName;
        mTempCPUTimeMap = new HashMap<>();
        mOutputReader = ProcessCommand.runRootCommand(createCommand(), false);

        if (mOutputReader != null)
            dump();
    }

    @Override
    protected String[] createCommand()
    {
        String[] command = {DUMPSYS + COMMAND + mPackageName};
        return command;
    }

    @Override
    public void dump()
    {
        boolean estimatePowerSection = false;
        boolean isCPUSection = true;
        String packageName = null;
        Double estimatedPowerUsage = null;
        String uniqueId = null;

        Collections.reverse(mOutputReader);

        for(String lineRead : mOutputReader)
        {
            if(isCPUSection)
            {
                if (packageName == null)
                    packageName = parseProcessUsagePackage(lineRead);

                else
                {
                    CPUStats cpuStats = parseCPUInformation(lineRead);

                    if (cpuStats != null)
                    {
                        mTempCPUTimeMap.put(packageName, cpuStats);
                        packageName = null;
                    }
                }
            }

            if(uniqueId == null)
                uniqueId = packageUniqueId(lineRead);
            else
                isCPUSection = false;
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
                        retVal += (Double.parseDouble(time) * 3600) / 1000;
                        break;

                    case MINUTE:
                        time = time.replace(kMinute, "");
                        retVal += (Double.parseDouble(time) * 60) / 1000;
                        break;

                    case SECOND:
                        time = time.replace(kSecond, "");
                        retVal += Double.parseDouble(time) / 1000;
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
            retVal = matcher.group(0);

        return retVal;
    }

    private boolean isEstimatePowerSection(String info)
    {
        boolean isEstimatePowerSection = false;

        Matcher match = mEstimatePowerSectionPattern.matcher(info);

        if (match.find())
            isEstimatePowerSection = true;

        return isEstimatePowerSection;
    }


    private Double parsePowerSection(String info)
    {
        Double estimatedPowerUsage = null;

        String uidPowerUsage = parseUidPowerUsage(info);

        if (uidPowerUsage != null)
        {
            String[] split = uidPowerUsage.split(":");

            if (split.length == 2)
            {
                Double batteryPercentageUsed = (100 * Double.parseDouble(split[1].trim())) / mBatteryCapacity;
                mHashData.put(split[0].trim(), batteryPercentageUsed);
            }
        }

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

    private String parseUidPowerUsage(String info)
    {
        String retVal = null;

        Matcher matcher = mUidPowerUsagePattern.matcher(info);

        if (matcher.find())
        {
            retVal = matcher.group(0).replace("Uid", "");
            retVal = retVal.trim();
        }

        return retVal;
    }
}
