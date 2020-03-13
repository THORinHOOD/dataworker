package com.thorinhood.dataworker.utils.common;

public interface CallbackRunnable extends Runnable {
    public void callback();
    public void error(Exception e);
}