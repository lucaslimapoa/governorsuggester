package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lucas on 10/22/2015.
 */
public class BatteryDump extends AbstractDump
{
    private final String COMMAND = "batterystats";

    // Member variables
    private List<String> mOutputReader;
    private boolean mEstimatePowerSection;

    private Pattern mEstimatePowerSectionPattern = Pattern.compile("(Estimated power use) \\(mAh\\):");
    private Pattern mUidPowerUsagePattern = Pattern.compile("(Uid) \\w+: ?\\w+.?\\w+");
    private Pattern mBatteryCapacityPattern = Pattern.compile("(?!Capacity: )\\d+");

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
            parsePowerSection();
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    private void parsePowerSection() throws IOException
    {
        Double batteryCapacity = null;

        for(String lineRead : mOutputReader)
        {
            if (mEstimatePowerSection == false)
                mEstimatePowerSection = isEstimatePowerSection(lineRead);

            else
            {
                if (batteryCapacity == null)
                {
                    batteryCapacity = parseBatteryCapacity(lineRead);
                    continue;
                }

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
}
