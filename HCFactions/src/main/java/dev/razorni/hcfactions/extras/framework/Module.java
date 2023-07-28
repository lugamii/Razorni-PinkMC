package dev.razorni.hcfactions.extras.framework;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.extra.Configs;
import lombok.Getter;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.lang.reflect.Method;

@Getter
public abstract class Module<T extends Manager> extends Configs implements Listener {
    protected T manager;
    protected HCF instance;

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
