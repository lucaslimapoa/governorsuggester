package com.tcc.lucas.governorsuggestor;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by Lucas on 9/10/2015.
 */
public class ProcessUsage extends HashMap<String, String>
{
    private final String LOG_TAG = getClass().getSimpleName();

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
    static final String GROUPS = "Groups";
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
    static final String CPU_UTIME = "utime";
    static final String CPU_STIME = "stime";
    static final String CPU_CTIME = "ctime";
    static final String GUEST_TIME = "guest_time";
    static final String CGUEST_TIME = "cguest_time";

    private final String SEPARATOR_MEMORY = "kB";
    private final int POSITION_CPU_UTIME = 13;
    private final int POSITION_CPU_STIME = 14;
    private final int POSITION_CPU_CTIME = 15;
    private final int POSITION_GUEST_TIME = 42;
    private final int POSITION_CGUEST_TIME = 43;

    private String mProcessId;

    public ProcessUsage(String processId)
    {
        super();

        mProcessId = processId;

        try
        {
            getStatusFileInformation();
            getStatFileInformation();
        }

        catch (IOException e)
        {
            Log.e(LOG_TAG, "Cannot open status file - " + e.getLocalizedMessage());
            e.printStackTrace();
        }
    }

    private String cleanString(String key, String value)
    {
        String textCleaned = null;

        if(value != null)
        {
            textCleaned = value.replace(SEPARATOR_MEMORY, "");
            textCleaned = textCleaned.replace("\t", "");

            if(key.equals(GROUPS) == false)
                textCleaned = textCleaned.trim();
        }

        return textCleaned;
    }

    private void getStatusFileInformation() throws IOException
    {
        File statusFile = new File(mProcessId + "/" + Definitions.FILE_PROCESS_STATUS);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));
        String line = null;

        while ((line = bufferedReader.readLine()) != null)
        {
            String[] separatedText = line.split(Definitions.SEPARATOR_FILE_STATUS);
            String cleanedText = cleanString(separatedText[0], separatedText[1]);

            put(separatedText[0], cleanedText);
        }
    }

    private void getStatFileInformation() throws IOException
    {
        File statusFile = new File(mProcessId + "/" + Definitions.FILE_PROCESS_STAT);

        BufferedReader bufferedReader = new BufferedReader(new FileReader(statusFile));

        String[] processStats = bufferedReader.readLine().split(" ");

        if(processStats.length >= POSITION_CGUEST_TIME)
        {
            put(CPU_UTIME, processStats[POSITION_CPU_UTIME]);
            put(CPU_STIME, processStats[POSITION_CPU_STIME]);
            put(CPU_CTIME, processStats[POSITION_CPU_CTIME]);
            put(GUEST_TIME, processStats[POSITION_GUEST_TIME]);
            put(CGUEST_TIME, processStats[POSITION_CGUEST_TIME]);
        }
    }
}
