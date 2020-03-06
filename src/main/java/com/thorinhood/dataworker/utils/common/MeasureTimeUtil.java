package com.thorinhood.dataworker.utils.common;

public class MeasureTimeUtil {

    private long startTime;

    public void start() {
        startTime = System.nanoTime();
    }

    public long resultMilliseconds() {
        return (System.nanoTime() - startTime) / 1000000L;
    }

    public String end(String info) {
        return String.format(info, resultMilliseconds());
    }

}
