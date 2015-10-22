package com.tcc.lucas.governorsuggestor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by Lucas on 10/22/2015.
 */
public class ProcessCommand
{
    private static final String LOG_TAG = "ProcessCommand";

    static public BufferedReader runRootCommand(String[] commandList)
    {
        BufferedReader outputReader = null;

        try
        {
            Process rootProcess = Runtime.getRuntime().exec("su");
            DataOutputStream outputStream = new DataOutputStream(rootProcess.getOutputStream());

            for (String cmd : commandList)
            {
                outputStream.writeBytes(cmd + "\n");
            }

            outputStream.writeBytes("exit\n");
            outputStream.flush();

            outputReader = new BufferedReader(new InputStreamReader(rootProcess.getInputStream()));
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot run the command as root");
            e.printStackTrace();
        }

        return outputReader;
    }
}
