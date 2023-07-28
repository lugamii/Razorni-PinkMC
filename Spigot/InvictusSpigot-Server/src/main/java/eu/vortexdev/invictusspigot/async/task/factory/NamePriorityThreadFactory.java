package eu.vortexdev.invictusspigot.async.task.factory;

import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class NamePriorityThreadFactory implements ThreadFactory {

    private String name = "Spigot_Async-Thread";
    private final int priority;
    private int idCounter;
    private boolean isDaemon;
    private Queue<WeakReference<Thread>> createdThreadList;

    public NamePriorityThreadFactory(int priority) {
        this.priority = Math.min(Math.max(priority, Thread.MIN_PRIORITY), Thread.MAX_PRIORITY);
    }

    public NamePriorityThreadFactory(int priority, String name) {
        this(priority);
        this.name = name;
    }

    public NamePriorityThreadFactory setDaemon(boolean daemon) {
        isDaemon = daemon;
        return this;
    }

    public NamePriorityThreadFactory setLogThreads(boolean log) {
        if (log) {
            if (createdThreadList == null) {
                createdThreadList = new ConcurrentLinkedQueue<>();
            }
        } else {
            createdThreadList = null;
        }
        return this;
    }

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = Executors.defaultThreadFactory().newThread(runnable);
        thread.setPriority(priority);
        thread.setName(name + "-" + idCounter);
        thread.setDaemon(isDaemon);
        if (createdThreadList != null) {
            createdThreadList.add(new WeakReference<>(thread));
        }
        idCounter++;
        return thread;
    }

    public int getActiveCount() {
        if (createdThreadList != null) {
            Iterator<WeakReference<Thread>> iter = createdThreadList.iterator();
            int count = 0;
            while (iter.hasNext()) {
                WeakReference<Thread> ref = iter.next();
                Thread t = ref.get();
                if (t == null) {
                    iter.remove();
                } else if (t.isAlive()) {
                    count++;
                }
            }
            return count;
        }
        return -1;
    }

    public Queue<WeakReference<Thread>> getThreadList() {
        return createdThreadList;
    }
}