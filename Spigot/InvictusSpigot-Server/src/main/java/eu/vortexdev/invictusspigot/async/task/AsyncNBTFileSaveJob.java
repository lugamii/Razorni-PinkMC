package eu.vortexdev.invictusspigot.async.task;

import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;

import java.io.File;
import java.io.FileOutputStream;

public class AsyncNBTFileSaveJob implements Runnable {

    private NBTTagCompound compound;
    private File file;

    public AsyncNBTFileSaveJob(NBTTagCompound compound, File file) {
        this.compound = compound;
        this.file = file;
    }

    @Override
    public void run() {
        try (FileOutputStream fileoutputstream = new FileOutputStream(file)) {
            NBTCompressedStreamTools.a(compound, fileoutputstream);
        } catch (Exception e) {
            e.printStackTrace();
        }
        compound = null;
        file = null;
    }
}