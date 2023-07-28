package dev.razorni.hcfactions.listeners.type;

import dev.razorni.hcfactions.extras.framework.Config;
import dev.razorni.hcfactions.extras.framework.Module;
import dev.razorni.hcfactions.listeners.ListenerManager;
import dev.razorni.hcfactions.utils.Tasks;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BrewingStand;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SmeltListener extends Module<ListenerManager> {
    private final Map<Location, Furnace> furnaces;
    private final Map<Location, BrewingStand> brewingStands;

    public SmeltListener(ListenerManager manager) {
        super(manager);
        this.furnaces = new HashMap<>();
        this.brewingStands = new HashMap<>();
        Tasks.executeScheduled(manager, 2, this::tick);
    }

    @EventHandler
    public void onBrew(BrewEvent event) {
        Block block = event.getBlock();
        if (!this.brewingStands.containsKey(block.getLocation())) {
            this.brewingStands.put(block.getLocation(), (BrewingStand) block.getState());
        }
    }

    @EventHandler
    public void onBurn(FurnaceBurnEvent event) {
        Block block = event.getBlock();
        if (!this.furnaces.containsKey(block.getLocation())) {
            this.furnaces.put(block.getLocation(), (Furnace) block.getState());
        }
    }

    private void tick() {
        Iterator<Furnace> iterator = this.furnaces.values().iterator();
        while (iterator.hasNext()) {
            Furnace furnace = iterator.next();
            furnace.setCookTime((short) (furnace.getCookTime() + Config.SMELT_MULTIPLIER));
            furnace.update();
            if (furnace.getBurnTime() <= 1) {
                iterator.remove();
            }
        }
        Iterator<BrewingStand> stands = this.brewingStands.values().iterator();
        while (stands.hasNext()) {
            BrewingStand stand = stands.next();
            if (!stand.getChunk().isLoaded()) {
                stands.remove();
            } else {
                if (stand.getBrewingTime() <= 1) {
                    continue;
                }
                stand.setBrewingTime(Math.max(1, stand.getBrewingTime() - Config.SMELT_MULTIPLIER));
            }
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }
        if (event.getClickedBlock() == null) {
            return;
        }
        Block block = event.getClickedBlock();
        if (block.getType().name().contains("FURNACE")) {
            Furnace furnace = (Furnace) block.getState();
            this.furnaces.put(block.getLocation(), furnace);
        } else if (block.getState().getType().name().contains("BREWING_STAND")) {
            BrewingStand stand = (BrewingStand) block.getState();
            this.brewingStands.put(block.getLocation(), stand);
        }
    }
}
