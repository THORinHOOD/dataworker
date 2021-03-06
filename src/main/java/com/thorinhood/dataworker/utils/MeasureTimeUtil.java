package com.thorinhood.dataworker.utils;

import org.slf4j.Logger;

import java.util.function.BiFunction;
import java.util.function.Consumer;
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

    public void measure(Runnable runnable, Logger logger, String info, long count) {
        loggerStart(logger, info, count);
        start();
        runnable.run();
        loggerEnd(logger, info, count);
    }

    public <OUTPUT> OUTPUT measure(Supplier<OUTPUT> supplier, Logger logger, String info, long count) {
        loggerStart(logger, info, count);
        start();
        OUTPUT result = supplier.get();
        loggerEnd(logger, info, count);
        return result;
    }

    public <INPUT, OUTPUT> OUTPUT measure(Function<INPUT, OUTPUT> function, INPUT input, Logger logger, String info,
                                          long count) {
        loggerStart(logger, info, count);
        start();
        OUTPUT result = function.apply(input);
        loggerEnd(logger, info, count);
        return result;
    }

    public <INPUT1, INPUT2, OUTPUT> OUTPUT measure(BiFunction<INPUT1, INPUT2, OUTPUT> function, INPUT1 input1,
                                                   INPUT2 input2, Logger logger, String info, long count) {
        loggerStart(logger, info, count);
        start();
        OUTPUT result = function.apply(input1, input2);
        loggerEnd(logger, info, count);
        return result;
    }

    public <INPUT> void measure(Consumer<INPUT> function, INPUT input, Logger logger, String info, long count) {
        loggerStart(logger, info, count);
        start();
        function.accept(input);
        loggerEnd(logger, info, count);
    }

    private void loggerEnd(Logger logger, String info, long count) {
        logger.info(String.format("End [%s] [%d] [%d ms]", info, count, resultMilliseconds()));
    }

    private void loggerStart(Logger logger, String info, long count) {
        logger.info(String.format("Start [%s] [%d]", info, count));
    }
}
