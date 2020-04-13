package com.thorinhood.dataworker.utils;

public interface CallbackRunnable extends Runnable {
    public void callback();
    public void error(Exception e);
}
