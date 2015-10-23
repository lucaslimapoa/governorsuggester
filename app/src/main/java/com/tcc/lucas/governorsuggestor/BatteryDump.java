package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 10/22/2015.
 */
public class BatteryDump extends AbstractDump
{
    public BatteryDump()
    {

    }

    @Override
    protected String[] createCommand()
    {
        return new String[0];
    }

    @Override
    public void dump()
    {

    }

    protected boolean isInformationValid(String info)
    {
        return false;
    }
}
