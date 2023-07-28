package dev.razorni.hub.framework;

import dev.razorni.hub.Hub;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

@Getter
public abstract class Module<T extends Manager> implements Listener {
    protected T manager;
    protected Hub instance;

    public Module(T manager) {
        this.instance = manager.getInstance();
        this.manager = manager;
        this.checkListener();
    }

    private void checkListener() {
        Method[] methods = this.getClass().getMethods();
        for (Method method : methods) {
            if (method.isAnnotationPresent(EventHandler.class)) {
                this.manager.registerListener(this);
                break;
            }
        }
    }
}
