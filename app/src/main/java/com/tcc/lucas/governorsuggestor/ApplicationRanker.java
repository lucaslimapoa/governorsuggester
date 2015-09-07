package com.tcc.lucas.governorsuggestor;

import android.content.pm.ApplicationInfo;
import android.net.TrafficStats;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class ApplicationRanker {

    public ApplicationRanker(){
    }

    public Application rankApplication(ApplicationInfo application){
        Application rankedApplication = new Application();

        // Network Information
        rankedApplication.setBytesReceived(TrafficStats.getUidRxBytes(application.uid));
        rankedApplication.setBytesReceived(TrafficStats.getUidTxBytes(application.uid));

        return rankedApplication;
    }

    public List<Application> rankApplication(List<ApplicationInfo> applicationList){

        List<Application> rankedApplicationsList = new ArrayList<Application>();

        for (ApplicationInfo app : applicationList) {
            Application newApplication = rankApplication(app);
            rankedApplicationsList.add(newApplication);
        }

        return rankedApplicationsList;
    }
}
