package net.minecraft.server;

import com.eatthepath.uuid.FastUUID;
import eu.vortexdev.invictusspigot.InvictusSpigot;
import eu.vortexdev.invictusspigot.async.task.AsyncPlayerDataSaveJob;
import eu.vortexdev.invictusspigot.async.task.AsyncWorldDataSaveJob;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.bukkit.craftbukkit.entity.CraftPlayer;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class WorldNBTStorage implements IDataManager, IPlayerFileData {

    private static final Logger a = LogManager.getLogger();
    private final File baseDir;
    private final File playerDir;
    private final File dataDir;
    private final long sessionId = MinecraftServer.az();
    private final String f;
    private UUID uuid = null; // CraftBukkit

    public WorldNBTStorage(File file, String s, boolean flag) {
        this.baseDir = new File(file, s);
        this.baseDir.mkdirs();
        this.playerDir = new File(this.baseDir, "playerdata");
        this.dataDir = new File(this.baseDir, "data");
        this.dataDir.mkdirs();
        this.f = s;
        if (flag) {
            this.playerDir.mkdirs();
        }

        this.h();
        
        try {
            checkSession0();
        } catch (Throwable t) {
//            org.spigotmc.SneakyThrow.sneaky(t);
        }
    }

    private void h() {
        try {
            File file = new File(this.baseDir, "session.lock");

            try (DataOutputStream dataoutputstream = new DataOutputStream(new FileOutputStream(file))) {
                dataoutputstream.writeLong(this.sessionId);
            }

        } catch (IOException ioexception) {
            ioexception.printStackTrace();
            throw new RuntimeException("Failed to check session lock for world located at " + this.baseDir + ", aborting. Stop the server and delete the session.lock in this world to prevent further issues."); // Spigot
        }
    }

    public File getDirectory() {
        return this.baseDir;
    }

    public void checkSession() {
    }
    
    private void checkSession0() throws ExceptionWorldConflict {
        try {
            File file1 = new File(this.baseDir, "session.lock");

            try (DataInputStream datainputstream = new DataInputStream(new FileInputStream(file1))) {
                if (datainputstream.readLong() != this.sessionId) {
                    throw new ExceptionWorldConflict("The save for world located at " + this.baseDir + " is being accessed from another location, aborting");  // Spigot
                }
            }
        } catch (IOException ioexception) {
            throw new ExceptionWorldConflict("Failed to check session lock for world located at " + this.baseDir + ", aborting. Stop the server and delete the session.lock in this world to prevent further issues."); // Spigot
        }
    }
    
    public IChunkLoader createChunkLoader(WorldProvider worldprovider) {
        throw new RuntimeException("Old Chunk Storage is no longer supported.");
    }

    public WorldData getWorldData() {
        File file = new File(this.baseDir, "level.dat");
        NBTTagCompound nbttagcompound;
        NBTTagCompound nbttagcompound1;

        if (file.exists()) {
            try {
                nbttagcompound = NBTCompressedStreamTools.a(new FileInputStream(file));
                nbttagcompound1 = nbttagcompound.getCompound("Data");
                return new WorldData(nbttagcompound1);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }

        file = new File(this.baseDir, "level.dat_old");
        if (file.exists()) {
            try {
                nbttagcompound = NBTCompressedStreamTools.a(new FileInputStream(file));
                nbttagcompound1 = nbttagcompound.getCompound("Data");
                return new WorldData(nbttagcompound1);
            } catch (Exception exception1) {
                exception1.printStackTrace();
            }
        }

        return null;
    }

    public void saveWorldData(WorldData worlddata, NBTTagCompound nbttagcompound) {
        InvictusSpigot.INSTANCE.getThreadingManager().saveNBTData(new AsyncWorldDataSaveJob(worlddata, nbttagcompound, baseDir));
    }
    
    private final PlayerCache<File, NBTTagCompound> dataCache = new PlayerCache<>();

    public void saveWorldData(WorldData worlddata) {
        InvictusSpigot.INSTANCE.getThreadingManager().saveNBTData(new AsyncWorldDataSaveJob(worlddata, new NBTTagCompound(), baseDir));
    }

    public void save(EntityHuman entityhuman) {
        try {
            NBTTagCompound nbttagcompound = new NBTTagCompound();

            entityhuman.e(nbttagcompound);
            
            File file1 = new File(this.playerDir, FastUUID.toString(entityhuman.getUniqueID()) + ".dat");
            synchronized(this.dataCache) {
                this.dataCache.put(file1, nbttagcompound);
            }
            InvictusSpigot.INSTANCE.getThreadingManager().saveNBTData(new AsyncPlayerDataSaveJob(file1, nbttagcompound));
        } catch (Exception exception) {
            WorldNBTStorage.a.warn("Failed to save player data for " + entityhuman.getName());
        }

    }

    public NBTTagCompound load(EntityHuman entityhuman) {
        NBTTagCompound nbttagcompound = null;

        try {
            File file = new File(this.playerDir, FastUUID.toString(entityhuman.getUniqueID()) + ".dat");
            // Spigot Start
            boolean usingWrongFile = false;
            boolean normalFile = file.isFile(); // Vortex - Remove file.exists
            
            NBTTagCompound playerdata;
            synchronized(this.dataCache) {
                playerdata = this.dataCache.get(file);
            }
            
            if ( org.bukkit.Bukkit.getOnlineMode() && !normalFile && playerdata == null) // Paper - Check online mode first // Akarin - ensures normal file
            {
                file = new File( this.playerDir, FastUUID.toString(UUID.nameUUIDFromBytes( ( "OfflinePlayer:" + entityhuman.getName() ).getBytes(StandardCharsets.UTF_8) )) + ".dat");
                synchronized(this.dataCache) {
                    playerdata = this.dataCache.get(file);
                }
                if ( file.exists() )
                {
                    usingWrongFile = true;
                    org.bukkit.Bukkit.getServer().getLogger().warning( "Using offline mode UUID file for player " + entityhuman.getName() + " as it is the only copy we can find." );
                }
            }
            // Spigot End

            if (playerdata != null) {
                nbttagcompound = playerdata;
            } else if (normalFile) {
                nbttagcompound = NBTCompressedStreamTools.a(new FileInputStream(file));
            }
            // Spigot Start
            if ( usingWrongFile )
            {
                file.renameTo( new File( file.getPath() + ".offline-read" ) );
            }


        } catch (Exception exception) {
            WorldNBTStorage.a.warn("Failed to load player data for " + entityhuman.getName());
        }

        if (nbttagcompound != null) {
            // CraftBukkit start
            if (entityhuman instanceof EntityPlayer) {
                CraftPlayer player = (CraftPlayer) entityhuman.getBukkitEntity();
                // Only update first played if it is older than the one we have
                long modified = new File(this.playerDir, FastUUID.toString(entityhuman.getUniqueID()) + ".dat").lastModified();
                if (modified < player.getFirstPlayed()) {
                    player.setFirstPlayed(modified);
                }
            }
            // CraftBukkit end

            entityhuman.f(nbttagcompound);
        }

        return nbttagcompound;
    }

    // CraftBukkit start
    public NBTTagCompound getPlayerData(String s) {
        try {
            File file1 = new File(this.playerDir, s + ".dat");

            if (file1.exists()) {
                return NBTCompressedStreamTools.a(new FileInputStream(file1));
            }
        } catch (Exception exception) {
            a.warn("Failed to load player data for " + s);
        }

        return null;
    }
    // CraftBukkit end

    public IPlayerFileData getPlayerFileData() {
        return this;
    }

    public String[] getSeenPlayers() {
        String[] astring = this.playerDir.list();

        if (astring == null) {
            astring = new String[0];
        }

        for (int i = 0; i < astring.length; ++i) {
            if (astring[i].endsWith(".dat")) {
                astring[i] = astring[i].substring(0, astring[i].length() - 4);
            }
        }

        return astring;
    }

    public void a() {}

    public File getDataFile(String s) {
        return new File(this.dataDir, s + ".dat");
    }

    public String g() {
        return this.f;
    }

    // CraftBukkit start
    public UUID getUUID() {
        if (uuid != null) return uuid;
        File file1 = new File(this.baseDir, "uid.dat");
        if (file1.exists()) {
            try (DataInputStream dis = new DataInputStream(new FileInputStream(file1))) {
                return uuid = new UUID(dis.readLong(), dis.readLong());
            } catch (IOException ex) {
                a.warn("Failed to read " + file1 + ", generating new random UUID", ex);
            }
            // NOOP
        }
        uuid = UUID.randomUUID();
        try (DataOutputStream dos = new DataOutputStream(new FileOutputStream(file1))) {
            dos.writeLong(uuid.getMostSignificantBits());
            dos.writeLong(uuid.getLeastSignificantBits());
        } catch (IOException ex) {
            a.warn("Failed to write " + file1, ex);
        }
        // NOOP
        return uuid;
    }

    public File getPlayerDir() {
        return playerDir;
    }
    // CraftBukkit end
}
