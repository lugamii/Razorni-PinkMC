package eu.vortexdev.invictusspigot.async;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import eu.vortexdev.invictusspigot.async.task.AsyncNBTFileSaveJob;
import eu.vortexdev.invictusspigot.async.task.AsyncTaskQueueWorker;
import eu.vortexdev.invictusspigot.async.task.factory.NamePriorityThreadFactory;
import eu.vortexdev.invictusspigot.async.task.factory.NameThreadFactory;
import net.minecraft.server.NBTTagCompound;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.Iterator;
import java.util.Queue;
import java.util.concurrent.*;

public class ThreadingManager {

    private final NamePriorityThreadFactory fileThreadPoolFactory = new NamePriorityThreadFactory(Thread.currentThread().getPriority() - 1, "Spigot_Async-File-Executor").setLogThreads(true).setDaemon(true);
    private final ExecutorService loginPool = new ThreadPoolExecutor(1, 64, 30, TimeUnit.SECONDS, new LinkedBlockingQueue<>(), new NameThreadFactory("Spigot_Login-Executor"));
    private final ExecutorService fileThreadPool = Executors.newCachedThreadPool(fileThreadPoolFactory);
    private final ExecutorService chatThreadPool = Executors.newCachedThreadPool(new NameThreadFactory("Spigot_Async-Chat-Executor"));
    private final ScheduledExecutorService spigotTickingThreadPool = new ScheduledThreadPoolExecutor(1);
    private final ScheduledExecutorService spigotThreadPool = new ScheduledThreadPoolExecutor(1);
    private final AsyncTaskQueueWorker nbtFiles = new AsyncTaskQueueWorker(fileThreadPool);
    private final ExecutorService trackerThreadPool = Executors.newFixedThreadPool(3, new ThreadFactoryBuilder().setNameFormat("Spigot_Async-Entity-Tracker-%d").build());

    public void saveNBTData(Runnable savejob) {
        nbtFiles.queueTask(savejob);
    }

    public void saveNBTFile(NBTTagCompound compound, File file) {
        nbtFiles.queueTask(new AsyncNBTFileSaveJob(compound, file));
    }

    public void execute(Runnable runnable) {
        spigotThreadPool.execute(runnable);
    }

    public ScheduledExecutorService getSpigotTickingPool() {
        return spigotTickingThreadPool;
    }

    public ScheduledExecutorService getSpigotPool() {
        return spigotThreadPool;
    }

    public ExecutorService getChatThreadPool() {
        return chatThreadPool;
    }

    public ExecutorService getLoginPool() {
        return loginPool;
    }

    public ExecutorService getTrackerThreadPool() {
        return trackerThreadPool;
    }

    public void shutdown() {
        spigotThreadPool.shutdown();
        fileThreadPool.shutdown();
        chatThreadPool.shutdown();
        while (nbtFiles.isActive() && !fileThreadPool.isTerminated()) {
            try {
                fileThreadPool.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException ignored) {
            }
        }
        if (!fileThreadPool.isTerminated()) {
            fileThreadPool.shutdownNow();
            try {
                fileThreadPool.awaitTermination(10L, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if (fileThreadPoolFactory.getActiveCount() > 0) {
                Queue<WeakReference<Thread>> queue = fileThreadPoolFactory.getThreadList();
                Iterator<WeakReference<Thread>> iterator = null;
                if (queue != null) {
                    iterator = queue.iterator();
                }
                while (iterator != null && iterator.hasNext()) {
                    Thread thread = iterator.next().get();
                    if (thread == null) {
                        iterator.remove();
                    } else if (thread.isAlive()) {
                        for (StackTraceElement et : thread.getStackTrace()) {
                            System.out.println(et.toString());
                        }
                    }
                }
            }
        }
    }

}
