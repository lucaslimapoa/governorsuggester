package com.tcc.lucas.governorsuggestor;

import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lucas on 10/22/2015.
 */
abstract public class AbstractDump
{
    final String DUMPSYS = "dumpsys ";
    final String SEPARATOR = ",";

    protected HashMap<String, Object> mHashData;

    abstract protected String[] createCommand();
    abstract protected void dump() throws IOException;

    protected AbstractDump()
    {
        mHashData = new HashMap<>();
    }

    protected boolean containsKey(String key)
    {
        return mHashData.containsKey(key);
    }

    protected Object get(String key)
    {
        return mHashData.get(key);
    }
}
