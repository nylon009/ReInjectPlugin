package com.nylon.reinject;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;


public class ReInjectPlugin implements Plugin<Project> {
    private static final String TAG = "ReInjectPlugin";
    @Override
    public void apply(Project project) {
        AppExtension appExtension = project.getExtensions().findByType(AppExtension.class);
        if(appExtension == null){
            ReLog.d(TAG,"appExtension is null");
            return;
        }
        project.getExtensions().create("ReInjectConfig", ReInjectConfig.class);
        appExtension.registerTransform(new ReInjectTransform(project));
    }
}