package eu.vortexdev.invictusspigot.async.task;

import net.minecraft.server.NBTCompressedStreamTools;
import net.minecraft.server.NBTTagCompound;
import net.minecraft.server.WorldData;

import java.io.File;
import java.io.FileOutputStream;

//Vortex Async World Saving
public class AsyncWorldDataSaveJob implements Runnable {

    private WorldData target;
    private NBTTagCompound data;
    private File dir;

    public AsyncWorldDataSaveJob(WorldData target, NBTTagCompound data, File dir) {
        this.target = target;
        this.data = data;
        this.dir = dir;
    }

    @Override
    public void run() {
        NBTTagCompound nbttagcompound1 = target.a(data);
        NBTTagCompound nbttagcompound2 = new NBTTagCompound();
        nbttagcompound2.set("Data", nbttagcompound1);
        try {
            final File file1 = new File(this.dir, "level.dat_new");
            final File file2 = new File(this.dir, "level.dat_old");
            final File file3 = new File(this.dir, "level.dat");
            NBTCompressedStreamTools.a(nbttagcompound2, new FileOutputStream(file1));
            if (file2.exists())
                file2.delete();
            file3.renameTo(file2);
            if (file3.exists())
                file3.delete();
            file1.renameTo(file3);
            if (file1.exists())
                file1.delete();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        this.target = null;
        this.data = null;
        this.dir = null;
    }
}
