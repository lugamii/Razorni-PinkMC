package dev.razorni.hub.versions;

import dev.razorni.hub.Hub;
import dev.razorni.hub.framework.Manager;
import dev.razorni.hub.utils.shits.Logger;
import dev.razorni.hub.utils.shits.Utils;
import dev.razorni.hub.versions.type.Version1_16_R3;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

@Getter
public class VersionManager extends Manager {

    private final Version version;

    public VersionManager(Hub plugin) {
        super(plugin);
        this.version = this.setVersion();
    }

    public boolean isVer16() {
        return this.version instanceof Version1_16_R3;
    }

    public Version setVersion() {
        String classname = "dev.razorni.hub.versions.type.Version" + Utils.getNMSVer();
        try {
            return (Version) Class.forName(classname).getConstructor(VersionManager.class).newInstance(this);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            Bukkit.getServer().shutdown();
            Logger.print(Logger.LINE_CONSOLE, "&cVersion not supported.", Logger.LINE_CONSOLE);
            return null;
        }
    }
}
