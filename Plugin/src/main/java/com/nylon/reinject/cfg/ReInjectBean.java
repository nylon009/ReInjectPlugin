package com.nylon.reinject.cfg;

import java.util.ArrayList;
import java.util.List;

public class ReInjectBean {
    private List<Inject> injects = new ArrayList<>();

    /**
     * Get Inject list in json file
     * @return Inject list in the json file
     */
    public List<Inject> getInjects() {
        return injects;
    }
}
