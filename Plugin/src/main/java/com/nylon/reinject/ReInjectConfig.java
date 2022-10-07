package com.nylon.reinject;

import com.google.gson.Gson;
import com.nylon.reinject.cfg.ReInjectBean;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;

public class ReInjectConfig {
    private static final String TAG = "ReInjectConfig";
    public String reInjectFile = "ReInject.json";

    private ReInjectBean injectBean;

    public void load() {
        if (reInjectFile == null) {
            ReLog.d(TAG, "no reInjectFile");
            return;
        }
        File file = new File(reInjectFile);
        if (!file.exists()) {
            ReLog.d(TAG, "reInjectFile " + reInjectFile + " doesn't exist");
            return;
        }

        Reader reader = null;
        try {
            reader = new BufferedReader(new FileReader(reInjectFile));
            injectBean = new Gson().fromJson(reader, ReInjectBean.class);
        } catch (FileNotFoundException e) {
            ReLog.d(TAG, "failed to parse " + reInjectFile);
        }
    }

    public ReInjectBean getInjectBean() {
        return injectBean;
    }
}