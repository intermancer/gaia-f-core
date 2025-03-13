package com.intermancer.gaiaf.core.organism;

import java.util.ArrayList;
import java.util.List;

public class DataQuantum {
    private List<Double> values = new ArrayList<>();

    public void addValue(double value) {
        values.add(value);
    }

    public double getValue(int index) {
        return values.get(index % values.size());
    }
}