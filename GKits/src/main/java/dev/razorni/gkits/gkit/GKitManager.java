package dev.razorni.gkits.gkit;


import dev.razorni.gkits.GKits;
import dev.razorni.gkits.customenchant.CustomEnchant;
import dev.razorni.gkits.gkit.listener.GKitMenuListener;
import com.mongodb.Block;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.ReplaceOptions;
import lombok.Data;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.inventory.EquipmentSetEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

@Data
public class GKitManager {

    private final GKits plugin;
    private final Map<UUID, GKit> kitMap;
    private final Set<UUID> isEditing;

    public GKitManager(GKits plugin) {
        this.plugin = plugin;
        kitMap = new HashMap<>();
        isEditing = new HashSet<>();

        Bukkit.getPluginManager().registerEvents(new GKitMenuListener(plugin), plugin);
        loadGKits();
    }

    public void loadGKits() {
        plugin.getMongoManager().getGKits().find()
                .forEach((Block<? super Document>) document -> {
                    GKit gKit = new GKit(document);
                    kitMap.put(gKit.getUuid(), gKit);
                });
    }

    public void deleteGkit(GKit gKit, boolean async) {
        if (async) {
            plugin.getExecutorService().execute(() -> deleteGkit(gKit, false));
            return;
        }

        kitMap.remove(gKit.getUuid());
        plugin.getMongoManager().getGKits()
                .deleteMany(Filters.eq("uuid", gKit.getUuid().toString()));
    }

    public GKit createGkit(String name) {
        GKit gKit = new GKit(name);
        kitMap.put(gKit.getUuid(), gKit);
        return gKit;
    }

    public void saveGKits(boolean async) {
        kitMap.values().forEach(gKit -> saveGKit(gKit, async));
    }

    public void saveGKit(GKit gKit, boolean async) {
        if (async) {
            plugin.getExecutorService().execute(() -> saveGKit(gKit, false));
            return;
        }

        plugin.getMongoManager().getGKits().replaceOne(Filters.eq("uuid",
                gKit.getUuid().toString()), gKit.toBson(), new ReplaceOptions().upsert(true));
    }

    public void equip(Event event) {
        if (event instanceof EquipmentSetEvent) {
            EquipmentSetEvent equipmentSetEvent = (EquipmentSetEvent) event;
            Player player = (Player) equipmentSetEvent.getHumanEntity();
            ItemStack newItem = equipmentSetEvent.getNewItem();
            ItemStack previousItem = equipmentSetEvent.getPreviousItem();

            if (GKits.get().hasLore(previousItem)) {
                previousItem.getItemMeta().getLore().forEach(s -> {
                    CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

                    if (customEnchant != null)
                        customEnchant.remove(player);
                });
            }

            if (GKits.get().hasLore(newItem)) {
                newItem.getItemMeta().getLore().forEach(s -> {
                    CustomEnchant customEnchant = plugin.getCustomEnchantManager().getEnchantByDisplayName(s);

                    if (customEnchant != null)
                        customEnchant.apply(player);
                });

            }
        }
    }

    public boolean canUse(Player player) {
        return plugin.getPluginHook().canUseGKitCommand(player);
    }

    public GKit getGKit(UUID uuid) {
        return kitMap.get(uuid);
    }

    public GKit getGKit(String name) {
        for (GKit gkit : kitMap.values())
            if (gkit.getName().equalsIgnoreCase(name))
                return gkit;

        return null;
    }

    public boolean doesGKitExist(String name) {
        return getGKit(name) != null;
    }

}
