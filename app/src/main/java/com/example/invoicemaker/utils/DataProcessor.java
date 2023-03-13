package com.example.invoicemaker.utils;

import java.util.ArrayList;
import java.util.Iterator;

public class DataProcessor {
    private static DataProcessor dataProcessor;
    public String TAG = "DataProcessor";
    private ArrayList<ModelChangeListener> listeners = new ArrayList();

    private DataProcessor() {
    }

    public static DataProcessor getInstance() {
        if (dataProcessor == null) {
            dataProcessor = new DataProcessor();
        }
        return dataProcessor;
    }

    public void notifyListeners(String str, int i) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((ModelChangeListener) it.next()).onReceiveModelChange(str, i);
        }
    }

    public void addChangeListener(ModelChangeListener modelChangeListener) {
        this.listeners.add(modelChangeListener);
    }

    public void removeChangeListener(ModelChangeListener modelChangeListener) {
        try {
            this.listeners.remove(modelChangeListener);
        } catch (Exception unused) {
        }
    }
}
