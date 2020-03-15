package com.thorinhood.dataworker.utils.common;

import java.util.concurrent.Executor;

public class CallbackExecutor implements Executor {
    
    @Override
    public void execute(final Runnable r) {
        final Thread runner = new Thread(r);
        runner.start();
        if (r instanceof CallbackRunnable) {
            CallbackRunnable cr = (CallbackRunnable) r;
            Thread callerbacker = new Thread(() -> {
                try {
                    runner.join();
                    cr.callback();
                }
                catch (Exception e) {
                    cr.error(e);
                }
            });
            callerbacker.start();
        }
    }

}