package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Definitions
{
    // System Folders
    static final String FOLDER_SYS = "/sys/";
    static final String FOLDER_SYSTEM_CPU = FOLDER_SYS + "devices/system/cpu/cpu0/cpufreq/";

    // System CPU Files
    static final String FILE_SYSTEM_GOVERNOR = "scaling_governor";
    static final String FILE_SYSTEM_AVAILABLE_GOVERNORS = "scaling_available_governors";
    static final String FILE_SYSTEM_CURRENT_CPU_FREQ = "scaling_cur_freq";
    static final String FILE_SYSTEM_MIN_CPU_FREQ = "cpuinfo_min_freq";
    static final String FILE_SYSTEM_MAX_CPU_FREQ = "cpuinfo_max_freq";

    // Device Models

    static final String DEVICE_LG_G3 = "lg-d855";
    static final String DEVICE_NEXUS_5 = "Nexus 5";
    static final String DEVICE_MOTO_MAXX = "MOTOMAXX"; //TODO Fix this name

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
