package eu.vortexdev.invictusspigot.async.task.factory;

import java.util.concurrent.ThreadFactory;

public class NameThreadFactory implements ThreadFactory {
    private final String prefix;
    private int counter = 0;

    public NameThreadFactory(String prefix) {
        this.prefix = prefix;
    }

    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(runnable, prefix + "-" + counter++);
        thread.setName(prefix + "-" + counter);
        return thread;
    }
}