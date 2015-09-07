package com.tcc.lucas.governorsuggestor;

import android.content.pm.ApplicationInfo;

import java.util.List;

/**
 * Created by Lucas on 9/6/2015.
 */
public class ApplicationRanker {

    private List<Application> mApplicationsList;

    public ApplicationRanker(){

    }

    public List<Application> getApplicationsList() {
        return mApplicationsList;
    }

    public void setApplicationsList(List<Application> mApplicationsList) {
        this.mApplicationsList = mApplicationsList;
    }

    public Application rankApplication(ApplicationInfo application){
        return null;
    }

    public List<Application> rankApplication(List<ApplicationInfo> applicationList){
        return  null;
    }

}
