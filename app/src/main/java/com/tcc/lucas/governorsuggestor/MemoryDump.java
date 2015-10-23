package com.tcc.lucas.governorsuggestor;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lucas on 10/22/2015.
 */
public class MemoryDump extends AbstractDump
{
    // Meminfo separators
    public final String PROC = "proc";
    public final String SERVICE_A = "servicea";
    public final String SERVICE_B = "serviceb";
    public final String CACHED = "cached";

    // Member variables
    private HashMap<String, Double> mMemoryDump;
    private BufferedReader mOutputReader;

    public MemoryDump()
    {
        mMemoryDump = new HashMap<>();
        mOutputReader = ProcessCommand.runRootCommand(createCommand(), true);

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
        String[] command = {DUMPSYS + "meminfo -c"};
        return command;
    }

    @Override
    protected void dump() throws IOException
    {
        String lineRead;

        while ((lineRead = mOutputReader.readLine()) != null)
        {
            if(isInformationValid(lineRead))
            {
                String[] splittedLine = lineRead.split(SEPARATOR);

                if(splittedLine.length == 6)
                {
                    String packageName = splittedLine[2];
                    Double pss = Double.parseDouble(splittedLine[4]);

                    mMemoryDump.put(packageName, pss);
                }
            }
        }
    }

    @Override
    protected boolean isInformationValid(String info)
    {
        boolean retVal = false;

        if(info.contains(PROC))
        {
            if(info.contains(SERVICE_A) || info.contains(SERVICE_B) || info.contains(CACHED))
                retVal = true;
        }

        return retVal;
    }

    public Double get(String key)
    {
        return mMemoryDump.get(key);
    }
}
