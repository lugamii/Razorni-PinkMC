package dev.razorni.hcfactions.utils.versions;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.extras.framework.Manager;
import dev.razorni.hcfactions.utils.Logger;
import dev.razorni.hcfactions.utils.Utils;
import dev.razorni.hcfactions.utils.versions.type.Version1_16_R3;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.lang.reflect.InvocationTargetException;

@Getter
public class VersionManager extends Manager {

    private final Version version;

    public VersionManager(HCF plugin) {
        super(plugin);
        this.version = this.setVersion();
    }

    public boolean isVer16() {
        return this.version instanceof Version1_16_R3;
    }

    public Version setVersion() {
        String classname = "dev.razorni.hcfactions.utils.versions.type.Version" + Utils.getNMSVer();
        try {
            return (Version) Class.forName(classname).getConstructor(VersionManager.class).newInstance(this);
        } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException | ClassNotFoundException e) {
            Bukkit.getServer().shutdown();
            Logger.print(Logger.LINE_CONSOLE, "&cThis version is not supported.", Logger.LINE_CONSOLE);
            return null;
        }
    }
}
