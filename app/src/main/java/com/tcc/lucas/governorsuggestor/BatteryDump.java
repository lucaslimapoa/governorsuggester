package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/22/2015.
 */
public class BatteryDump extends AbstractDump
{
    private final String COMMAND = "batterystats";
    private final String kHour = "h";
    private final String kMinute = "m";
    private final String kSecond = "s";
    private final String kMillisecond = "ms";

    // Member variables
    private BufferedReader mOutputReader;
    private boolean mEstimatePowerSection;

    private Pattern mEstimatePowerSectionPattern = Pattern.compile("(Estimated power use) \\(mAh\\):");
    private Pattern mUidPowerUsagePattern = Pattern.compile("(Uid) \\w+: ?\\d.\\d+");
    private Pattern mBatteryCapacityPattern = Pattern.compile("(?!Capacity: )\\d+");
    private Pattern mProcessSectionPattern = Pattern.compile("(Proc) com([\\.*]\\w+)+(:\\w+)?\\w+:");
    private Pattern mCPUInfoPattern = Pattern.compile("\\d\\w+");
    private Pattern mTimeUnitPattern = Pattern.compile("(?!\\d)\\w+");

    enum TimeUnit
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
    public void dump()
    {
        try
        {
//            parsePowerSection();
            parseProcessCPUSection();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    private void parsePowerSection() throws IOException
    {
        String lineRead;
        Double batteryCapacity = null;

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            if (mEstimatePowerSection == false)
                mEstimatePowerSection = isEstimatePowerSection(lineRead);

            else
            {
                if (batteryCapacity == null)
                    batteryCapacity = parseBatteryCapacity(lineRead);

                String uidPowerUsage = parseUidPowerUsage(lineRead);

                if (uidPowerUsage != null)
                {
                    String[] split = uidPowerUsage.split(":");

                    if (split.length == 2)
                    {
                        Double batteryPercentageUsed = (100 * Double.parseDouble(split[1].trim())) / batteryCapacity;
                        mHashData.put(split[0].trim(), batteryPercentageUsed);
                    }
                }
            }
        }
    }

    private boolean isEstimatePowerSection(String info)
    {
        boolean isEstimatePowerSection = false;

        Matcher match = mEstimatePowerSectionPattern.matcher(info);

        if (match.find())
            isEstimatePowerSection = true;

        return isEstimatePowerSection;
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

    private void parseProcessCPUSection() throws IOException
    {
        String lineRead, packageName = null;

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            if (packageName == null)
                packageName = parseProcessUsagePackage(lineRead);

            else
            {
                parseCPUInformation(lineRead);
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

    private BatteryStats parseCPUInformation(String info)
    {
        BatteryStats retVal = null;

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
                    retVal = new BatteryStats();

                    double cpuUser = parseCPUUserTime(usrAndKrn[0]);
                    retVal.setCPUUser(cpuUser);

                    double cpuKernel = parseCPUKernelTime(usrAndKrn[1]);
                    retVal.setCPUKernel(cpuKernel);

                    double cpuForeground = parseCPUForegroundTime(split[1]);
                    retVal.setCPUForeground(cpuForeground);
                }
            }
        }

        return retVal;
    }

    private double parseCPUUserTime(String usr)
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

    private double parseCPUKernelTime(String usr)
    {
        double retVal = 0;

        //1s 730ms krn

        return retVal;
    }

    private double parseCPUForegroundTime(String usr)
    {
        double retVal = 0;


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
