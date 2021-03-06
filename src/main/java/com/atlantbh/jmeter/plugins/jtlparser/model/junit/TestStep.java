package com.atlantbh.jmeter.plugins.jtlparser.model.junit;

import java.util.HashMap;
import java.util.Map;

/*
 * This class is used for test steps information
 */
public class TestStep {

    private String name;
    private String time;
    private Map<String, String> assertionFailuresList = new HashMap<String, String>();

    public TestStep() {
        super();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Map<String, String> getAssertionFailuresList() {
        return assertionFailuresList;
    }

    public void setAssertionFailures(String message, String value) {
        this.assertionFailuresList.put(message, value);
    }
}
