package eu.vortexdev.invictusspigot.async.task;

import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;

import java.io.File;
import java.io.FileOutputStream;

public class AsyncPlayerDataSaveJob implements Runnable {

    private File target;
    private NBTTagCompound data;

    public AsyncPlayerDataSaveJob(File target, NBTTagCompound data) {
        this.target = target;
        this.data = data;
    }

    @Override
    public void run() {
        File temp = new File(target.getPath() + ".tmp");
        try (FileOutputStream fileoutputstream = new FileOutputStream(temp)) {
            NBTCompressedStreamTools.a(data, fileoutputstream);
            if (target.exists()) {
                target.delete();
            }
            temp.renameTo(target);
        } catch (Exception e) {
            e.printStackTrace();
        }
        target = null;
        data = null;
    }
}
