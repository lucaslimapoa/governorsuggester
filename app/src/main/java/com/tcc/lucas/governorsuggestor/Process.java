package com.tcc.lucas.governorsuggestor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lucas on 9/10/2015.
 */
public class Process
{
    static final String LOG_TAG = this.getClass().getSimpleName();

    enum State
    {
        SLEEPING, // S: Waiting for an event to complete
        RUNNING, // R: On run queue
        UNStringERRUPTABLE, // D: Usually I/O
        ZOMBIE, // Z: Terminated but not reaped by its parent
        STOPPED, // T: Either by a job control signal or because it is being traced
        DEAD // X: process is dead
    }

    static final String NAME = "Name";
    static final String STATE = "State";
    static final String TGID = "Tgid";
    static final String PID = "Pid";
    static final String PPID = "PPid";
    static final String TRACERPID = "TracerPid";
    static final String UID = "Uid";
    static final String GID = "Gid";
    static final String FDSIZE = "FDSize";
    static final String GROUP = "Groups";
    static final String VMPEAK = "VmPeak";
    static final String VMSIZE = "VmSize";
    static final String VMLCK = "VmLck";
    static final String VMPIN = "VmPin";
    static final String VMHWM = "VmHWM";
    static final String VMRSS = "VmRSS";
    static final String VMDATA = "VmData";
    static final String VMSTK = "VmStk";
    static final String VMEXE = "VmExe";
    static final String VMLIB = "VmLib";
    static final String VMPTE = "VmPMD";
    static final String VMPMD = "VmPTE";
    static final String VMSWAP = "VmSwap";
    static final String THREADS = "Threads";
    static final String SIGQ = "SigQ";
    static final String SIGPND = "SigPnd";
    static final String SHDPND = "ShdPnd";
    static final String SIGBLK = "SigBlk";
    static final String SIGIGN = "SigIgn";
    static final String SIGCGT = "SigCgt";
    static final String CAPINH = "CapInh";
    static final String CAPPRM = "CapPrm";
    static final String CAPEFF = "CapEff";
    static final String CAPBND = "CapBnd";
    static final String SECCOMP = "Seccomp";
    static final String CPUS_ALLOWED = "Cpus_allowed";
    static final String CPUS_ALLOWED_LIST = "Cpus_allowed_list";
    static final String MEMS_ALLOWED = "Mems_allowed";
    static final String MEMS_ALLOWED_LIST = "Mems_allowed_list";
    static final String VOLUNTARY_CTXT_SWITCHES = "voluntary_ctxt_switches";
    static final String NONVOLUNTARY_CTXT_SWITCHES = "nonvoluntary_ctxt_switches";

    private HashMap<String, Object> mProcessInformation;

    public Process(String filePath)
    {
        mProcessInformation = new HashMap<>();
        File statusFile = new File(filePath);

        try
        {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));
            String line = null;

            while ((line = bufferedReader.readLine()) != null)
            {
                String[] separatedText = line.split(Definitions.SEPARATOR_FILE_STATUS);
                mProcessInformation.put(separatedText[0], separatedText[1]);
            }
        }

        catch (FileNotFoundException e)
        {
            Log.e(LOG_TAG, "Status file does not exist - " + e.getLocalizedMessage());
            e.printStackTrace();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot open status file - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

}
