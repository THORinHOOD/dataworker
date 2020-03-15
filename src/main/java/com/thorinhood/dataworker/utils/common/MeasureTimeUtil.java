package com.thorinhood.dataworker.utils.common;

import org.slf4j.Logger;

import java.util.function.Function;
import java.util.function.Supplier;

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

    public void measure(Runnable runnable, Logger logger, String info) {
        logger.info(String.format("Start [%s]", info));
        start();
        runnable.run();
        logger.info(String.format("End [%s] [%d ms]", info, resultMilliseconds()));
    }

    public <OUTPUT> OUTPUT measure(Supplier<OUTPUT> supplier, Logger logger, String info) {
        loggerStart(logger, info);
        start();
        OUTPUT result = supplier.get();
        loggerEnd(logger, info);
        return result;
    }

    public <INPUT, OUTPUT> OUTPUT measure(Function<INPUT, OUTPUT> function, INPUT input, Logger logger, String info) {
        loggerStart(logger, info);
        start();
        OUTPUT result = function.apply(input);
        loggerEnd(logger, info);
        return result;
    }

    private void loggerEnd(Logger logger, String info) {
        logger.info(String.format("End [%s] [%d ms]", info, resultMilliseconds()));
    }

    private void loggerStart(Logger logger, String info) {
        logger.info(String.format("Start [%s]", info));
    }
}
