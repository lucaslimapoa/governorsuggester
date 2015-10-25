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

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            if(mEstimatePowerSection == false)
                mEstimatePowerSection = isEstimatePowerSection(lineRead);

            else
            {
                String uidPowerUsage = parseUidPowerUsage(lineRead);

                if(uidPowerUsage != null)
                {
                    String[] split = uidPowerUsage.split(":");

                    if(split.length == 2)
                        mHashData.put(split[0].trim(), Double.parseDouble(split[1].trim()));
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
