package com.tcc.lucas.governorsuggestor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 10/22/2015.
 */
public class ProcessCommand
{
    private static final String LOG_TAG = "ProcessCommand";

    static public List<String> runRootCommand(String[] commandList, boolean shouldBlockThread)
    {
        List<String> outputStringList = new ArrayList<>();

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

            if(shouldBlockThread)
                rootProcess.waitFor();

            BufferedReader outputReader = new BufferedReader(new InputStreamReader(rootProcess.getInputStream()));

            String lineRead;
            while ((lineRead = outputReader.readLine()) != null)
                outputStringList.add(lineRead);
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot run the command as root");
            e.printStackTrace();
        } catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        return outputStringList;
    }
}
