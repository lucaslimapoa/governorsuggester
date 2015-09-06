package com.tcc.lucas.governorsuggestor;

/**
 * Created by Lucas on 9/6/2015.
 */
public class Definitions {

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
