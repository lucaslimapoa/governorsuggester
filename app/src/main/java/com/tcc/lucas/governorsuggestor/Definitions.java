package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Definitions {

    // System Folders
    static final String FOLER_PROC = "/proc/";
    static final String FOLDER_SYS = "/sys/";

    // File Information
    static final String PROCESS_STAT = "stat";
    static final String NETWORK_RECEIVED_BYTES = "rx_bytes";
    static final String NETWORK_TRANSMITTED_BYTES = "tx_bytes";

    // Network Information
    static final String NETWORK_INTERFACE_WIRELESS = "/wlan0/";
    static final String NETWORK_WLAN_RECEIVED_TOTAL_BYTES = FOLDER_SYS + "class" + NETWORK_INTERFACE_WIRELESS + "net/statistics/" + NETWORK_RECEIVED_BYTES;
    static final String NETWORK_WLAN_TRANSMITTED_TOTAL_BYTES = FOLDER_SYS + "class" + NETWORK_INTERFACE_WIRELESS + "net/statistics/" + NETWORK_TRANSMITTED_BYTES;

    enum ApplicationRank{
        CPU_Bound,
        IO_Bound,
        RAM_Bound,
        Network_Bound
    }

    enum Governor{
        Interactive,
        Performance,
        Powersave,
        Ondemand
    }

}
