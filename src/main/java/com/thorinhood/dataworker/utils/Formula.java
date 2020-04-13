package com.thorinhood.dataworker.utils;

import java.util.Collections;
import java.util.List;

public class Formula {

    private final List<Double> w;
    private final List<Double> values;
    private final Double result;

    public Formula() {
        w = Collections.emptyList();
        values = Collections.emptyList();
        result = 0.0d;
    }

    public Formula(List<Double> w, List<Double> values) {
        this.w = w;
        this.values = values;
        double tmp = 0.0d;
        for (int i = 0; i < w.size(); i++) {
            tmp += w.get(i) * values.get(i);
        }
        result = tmp;
    }

    public Double getResult() {
        return result;
    }

    public List<Double> getW() {
        return w;
    }

    public List<Double> getValues() {
        return values;
    }

}
