package eu.vortexdev.invictusspigot.async.task;

import org.apache.logging.log4j.LogManager;

import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.ExecutorService;

public class AsyncTaskQueueWorker implements Runnable {

    private final ConcurrentLinkedDeque<Runnable> taskQueue = new ConcurrentLinkedDeque<>();
    private final ExecutorService service;
    private volatile boolean isActive = false;

    public AsyncTaskQueueWorker(ExecutorService service) {
        this.service = service;
    }

    @Override
    public void run() {
        Runnable task;
        while (isActive = (task = taskQueue.pollFirst()) != null) {
            try {
                task.run();
            } catch (Exception e) {
                LogManager.getLogger().error(
                        "Thread " + Thread.currentThread().getName() + " encountered an exception: " + e.getMessage(),
                        e);
            }
        }
    }

    public void queueTask(Runnable runnable) {
        taskQueue.addLast(runnable);
        if (!isActive) {
            isActive = true;
            service.execute(this);
        }
    }

    public boolean isActive() {
        if (!isActive && !taskQueue.isEmpty()) {
            isActive = true;
            service.execute(this);
        }
        return isActive;
    }

}