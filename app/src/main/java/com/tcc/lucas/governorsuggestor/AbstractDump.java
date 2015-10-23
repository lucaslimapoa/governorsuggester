package com.tcc.lucas.governorsuggestor;

import java.io.IOException;

/**
 * Created by Lucas on 10/22/2015.
 */
abstract public class AbstractDump
{
    final String DUMPSYS = "dumpsys ";
    final String SEPARATOR = ",";

    abstract protected String[] createCommand();
    abstract protected void dump() throws IOException;
}
