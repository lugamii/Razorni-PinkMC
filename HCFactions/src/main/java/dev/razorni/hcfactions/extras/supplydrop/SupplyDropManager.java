package dev.razorni.hcfactions.extras.supplydrop;

import dev.razorni.hcfactions.HCF;
import dev.razorni.hcfactions.utils.CC;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Created By LeandroSSJ
 * Created on 25/06/2021
 */
@Getter
@Setter
public class SupplyDropManager implements Listener {

    private File file;
    private FileConfiguration data;
    private List<ItemStack> aidropLoot = new ArrayList<>();
    private Location airdropLocation;
    private long lastTime;
    private int aidropspawnTime;
    private int disappearTime;

    public SupplyDropManager() {
        this.file = new File(HCF.getPlugin().getDataFolder(), "airdrop.yml");
        this.data = YamlConfiguration.loadConfiguration(this.file);

        this.aidropspawnTime = HCF.getPlugin().getConfig().getInt("AIRDROPS.SPAWN-TIME");
        this.disappearTime = HCF.getPlugin().getConfig().getInt("AIRDROPS.DISAPPEAR-TIME");
        this.lastTime = System.currentTimeMillis();
        HCF.getPlugin().getServer().getPluginManager().registerEvents(new SupplyDropMenu(), HCF.getPlugin());
        HCF.getPlugin().getServer().getPluginManager().registerEvents(this, HCF.getPlugin());

        HCF.getPlugin().getServer().getScheduler().runTaskTimer(HCF.getPlugin(), this::spawnAirdrop, 1200L * this.aidropspawnTime, 1200L * this.aidropspawnTime);
        this.loadAirdropItems();
    }

    public void loadAirdropItems() {
        /*

        Save in Object loot for aidrops

         */


        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        for (String path : this.data.getConfigurationSection("airdrop").getKeys(false)) {
            this.aidropLoot.add(this.data.getItemStack("airdrop." + path + ".item"));
        }
    }

    public void spawnAirdrop() {
        World world = Bukkit.getWorld("world");
        int x = 0;

        int z;
        for (z = 0; Math.abs(x) <= 100; x = HCF.RANDOM.nextInt(1000) - 500) {
        }

        while (Math.abs(z) <= 100) {
            z = HCF.RANDOM.nextInt(1000) - 500;
        }

        int y = world.getHighestBlockYAt(x, z);
        Block block = world.getBlockAt(x, y, z);
        if (block == null) {
            //this.spawnAirdrop();
            return;
        }
        Block realBlock = block.getRelative(BlockFace.UP);
        realBlock.setType(Material.ENDER_CHEST);
        realBlock.setMetadata("AirdropsA", new FixedMetadataValue(HCF.getPlugin(), new Object()));
        this.lastTime = System.currentTimeMillis();
        this.airdropLocation = realBlock.getLocation();
        for (String string : HCF.getPlugin().getConfig().getStringList("AIRDROPS.SPAWNED")) {
            string = string.replace("%x%", String.valueOf(x));
            string = string.replace("%z%", String.valueOf(z));
            string = string.replace("%y%", String.valueOf(realBlock.getLocation().getBlockY()));

            HCF.getPlugin().getServer().broadcastMessage(CC.translate(string));
        }
        HCF.getPlugin().getServer().getScheduler().runTaskLater(HCF.getPlugin(), this::removeAirdrop, 1200L * this.disappearTime);
    }

    public void removeAirdrop() {
        if (this.airdropLocation != null && this.airdropLocation.getBlock() != null && this.airdropLocation.getBlock().getType() == Material.ENDER_CHEST) {
            this.airdropLocation.getBlock().setType(Material.AIR);
            this.airdropLocation.getBlock().removeMetadata("AirdropsA", HCF.getPlugin());
            for (String string : HCF.getPlugin().getConfig().getStringList("AIRDROPS.REMOVED")) {
                string = string.replace("%x%", String.valueOf(this.airdropLocation.getBlockX()));
                string = string.replace("%z%", String.valueOf(this.airdropLocation.getBlockZ()));
                string = string.replace("%y%", String.valueOf(this.airdropLocation.getBlockY()));
                string = string.replace("%spawn_time%", String.valueOf(this.aidropspawnTime));

                HCF.getPlugin().getServer().broadcastMessage(CC.translate(string));
            }

            this.airdropLocation = null;
        }
    }

    @EventHandler(ignoreCancelled = false, priority = EventPriority.LOWEST)
    public void onInteract(PlayerInteractEvent event) {
        Block clickedBlock = event.getClickedBlock();
        if (event.getAction() != Action.LEFT_CLICK_BLOCK) {
            return;
        }

        if (clickedBlock == null || !clickedBlock.hasMetadata("AirdropsA")) {
            return;
        }
        for (int i = 0; i < HCF.getPlugin().getConfig().getInt("AIRDROPS.ITEM-AMOUNT"); ++i) {
            HCF.getPlugin().getServer().getWorld("world").dropItemNaturally(clickedBlock.getLocation(),
                    getAidropLoot().get(ThreadLocalRandom.current().nextInt(getAidropLoot().size())));
        }
        clickedBlock.removeMetadata("AirdropsA", HCF.getPlugin());
        clickedBlock.setType(Material.AIR);
        event.setCancelled(true);
        setAirdropLocation(null);
        for (String string : HCF.getPlugin().getConfig().getStringList("AIRDROPS.OPENED")) {
            string = string.replace("%x%", String.valueOf(clickedBlock.getLocation().getBlockX()));
            string = string.replace("%z%", String.valueOf(clickedBlock.getLocation().getBlockZ()));
            string = string.replace("%y%", String.valueOf(clickedBlock.getLocation().getBlockY()));
            string = string.replace("%player_name%", HCF.getPlugin().getRankManager().getRankColor(event.getPlayer())) + event.getPlayer().getName();


            HCF.getPlugin().getServer().broadcastMessage(CC.translate(string));
        }


    }

    @EventHandler
    public void onPluginShutdown(PluginDisableEvent event) {
        if (event.getPlugin() == HCF.getPlugin()) {
            removeAirdrop();
        }
    }
}
