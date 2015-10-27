package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/22/2015.
 */
public class BatteryDump extends AbstractDump
{
    private final String COMMAND = "batterystats";
    private boolean mEstimatePowerSection;

    // Member variables
    private BufferedReader mOutputReader;

    private Pattern mEstimatePowerSectionPattern = Pattern.compile("(Estimated power use) \\(mAh\\):");
    private Pattern mUidPowerUsagePattern = Pattern.compile("(Uid) \\w+: ?\\d.\\d+");
    private Pattern mBatteryCapacityPattern = Pattern.compile("(?!Capacity: )\\d+");
    private Pattern mCPUTimePattern = Pattern.compile("(Proc) com([\\.*]\\w+)+(:\\w+)?\\w+:");

    public BatteryDump()
    {
        super();

        mOutputReader = ProcessCommand.runRootCommand(createCommand(), false);

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
    public void dump() throws IOException
    {
        String lineRead;
        Double batteryCapacity = null;

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            if(mEstimatePowerSection == false)
                mEstimatePowerSection = isEstimatePowerSection(lineRead);

            else
            {
                if(batteryCapacity == null)
                    batteryCapacity = parseBatteryCapacity(lineRead);

                String uidPowerUsage = parseUidPowerUsage(lineRead);

                if(uidPowerUsage != null)
                {
                    String[] split = uidPowerUsage.split(":");

                    if(split.length == 2)
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

        if(match.find())
            isEstimatePowerSection = true;

        return isEstimatePowerSection;
    }

    private Double parseBatteryCapacity(String info)
    {
        Double retVal = null;

        Matcher matcher = mBatteryCapacityPattern.matcher(info);

        if(matcher.find())
        {
            String[] split = matcher.group(0).split(" ");

            if(split.length > 0)
                retVal = Double.parseDouble(split[0]);
        }

        return retVal;
    }

    private String parseUidPowerUsage(String info)
    {
        String retVal = null;

        Matcher matcher =  mUidPowerUsagePattern.matcher(info);

        if(matcher.find())
        {
            retVal = matcher.group(0).replace("Uid", "");
            retVal = retVal.trim();
        }

        return retVal;
    }
}
