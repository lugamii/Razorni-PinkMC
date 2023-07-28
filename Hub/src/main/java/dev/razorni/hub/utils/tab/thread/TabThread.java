package dev.razorni.hub.utils.tab.thread;

import dev.razorni.hub.utils.tab.TabAdapter;

/**
 * Created By LeandroSSJ
 * Created on 22/09/2021
 */
public class TabThread extends Thread {

    private final TabAdapter tabAdapter;

    public TabThread(TabAdapter tabAdapter) {
        setName("Hely - TabAdapter Thread");
        setDaemon(true);

        this.tabAdapter = tabAdapter;
        this.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                tabAdapter.getPlayerTablist().forEach((uniqueId, tab) -> tab.update());
            } catch (NullPointerException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(250L);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
