package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Definitions
{
    // System Folders
    static final String FOLER_PROC = "/proc/";
    static final String FOLDER_SYS = "/sys/";
    static final String FOLDER_SYSTEM_CPU = FOLDER_SYS + "devices/system/cpu/cpu0/cpufreq/";

    // Process Files
    static final String FILE_PROCESS_STAT = "stat";
    static final String FILE_PROCESS_STATUS = "status";
    static final String FILE_PROCESS_UPTIME = "uptime";
    static final String FILE_PROCESS_CMDLINE = "cmdline";
    static final String FILE_PROCESS_LOADAVG = "loadavg";
    static final String FILE_PROCESS_MEMINFO = "meminfo";
    static final String FILE_PROCESS_MVMINFO = "vmstat";

    // Network Files
    static final String FILE_NETWORK_RECEIVED_BYTES = "rx_bytes";
    static final String FILE_NETWORK_TRANSMITTED_BYTES = "tx_bytes";

    // System CPU Files
    static final String FILE_SYSTEM_GOVERNOR = "scaling_governor";
    static final String FILE_SYSTEM_AVAILABLE_GOVERNORS = "scaling_available_governors";
    static final String FILE_SYSTEM_CURRENT_CPU_FREQ = "scaling_cur_freq";
    static final String FILE_SYSTEM_MIN_CPU_FREQ = "cpuinfo_min_freq";
    static final String FILE_SYSTEM_MAX_CPU_FREQ = "cpuinfo_max_freq";

    // Separators
    static final String SEPARATOR_FILE_STATUS = ":";

    // Network Information
    static final String NETWORK_INTERFACE_WIRELESS = "/wlan0/";
    static final String NETWORK_WLAN_RECEIVED_TOTAL_BYTES = FOLDER_SYS + "class" + NETWORK_INTERFACE_WIRELESS + "net/statistics/" + FILE_NETWORK_RECEIVED_BYTES;
    static final String NETWORK_WLAN_TRANSMITTED_TOTAL_BYTES = FOLDER_SYS + "class" + NETWORK_INTERFACE_WIRELESS + "net/statistics/" + FILE_NETWORK_TRANSMITTED_BYTES;

    enum Governor
    {
        Interactive,
        Performance,
        Powersave,
        Ondemand,
        Conservative;

        @Override
        public String toString()
        {
            return super.toString();
        }
    }

}
