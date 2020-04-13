package com.thorinhood.dataworker.utils;

import com.google.common.collect.Lists;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MultiTool {

    public static <INPUT> void execute(Consumer<INPUT> function, INPUT input, Predicate<INPUT> conditionToExecute) {
        if (conditionToExecute.test(input)) {
            function.accept(input);
        }
    }

    public static <INPUT> void executeNotEmpty(Consumer<List<INPUT>> function, List<INPUT> inputs) {
        if (CollectionUtils.isNotEmpty(inputs)) {
            function.accept(inputs);
        }
    }

    public static <INPUT> void executeNotEmptyWithMeasure(Consumer<List<INPUT>> function,
                                                          List<INPUT> inputs,
                                                          String info,
                                                          MeasureTimeUtil measureTimeUtil,
                                                          Logger logger) {
        if (CollectionUtils.isNotEmpty(inputs)) {
            measureTimeUtil.measure(function, inputs, logger, info, inputs.size());
        }
    }

    public static <INPUT> void partition(List<INPUT> inputs, int batches, Consumer<List<INPUT>> batchProcessor) {
        if (CollectionUtils.isNotEmpty(inputs)) {
            Lists.partition(inputs, Math.max(inputs.size() / batches, inputs.size())).forEach(batchProcessor);
        }
    }

}
